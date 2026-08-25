package com.dexinsights.backend;

import com.dexinsights.backend.dto.ChatResponse;
import com.dexinsights.backend.dto.RetrievedContext;
import com.dexinsights.backend.model.Incident;
import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.model.StoreAddress;
import com.dexinsights.backend.model.Transaction;
import com.dexinsights.backend.service.ChatRetrievalService;
import com.dexinsights.backend.service.ChatService;
import com.dexinsights.backend.service.ContextAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ChatServiceTest {

    private ChatRetrievalService retrievalService;
    private ContextAssembler contextAssembler;
    private ChatService chatService;

    @BeforeEach
    void setUp() {

        retrievalService =
                Mockito.mock(ChatRetrievalService.class);

        contextAssembler =
                Mockito.mock(ContextAssembler.class);

        chatService =
                new ChatService(
                        retrievalService,
                        contextAssembler
                );
    }

    @Test
    void shouldGenerateGroundedStoreSummaryWithCitations() {

        Store store = new Store(
                "10001",
                "7-Eleven",
                "ONLINE",
                8,
                8,
                0,
                false,
                "2026-03-02T08:15:22.120Z",
                new StoreAddress("TX", "Dallas"),
                "32.7767",
                "-96.7970",
                0
        );

        Incident incident = new Incident(
                "INC-10001-0001",
                "10001",
                "2026-03-02T02:00:00.000Z",
                "LOW",
                "POS",
                "Minor POS issue",
                "RESOLVED"
        );

        Transaction transaction = new Transaction(
                "TX-10001-0001",
                "10001",
                "Regular",
                "50.00",
                "10.0",
                1,
                "2026-03-01T10:02:10.000Z",
                "2026-03-01T10:04:10.000Z"
        );

        RetrievedContext context =
                new RetrievedContext(
                        List.of(store),
                        List.of(incident),
                        List.of(transaction)
                );

        when(retrievalService.retrieve(
                "Summarize store 10001 health and recent activity",
                "10001"
        )).thenReturn(context);

        when(contextAssembler.assemble(context))
                .thenReturn("assembled context");

        ChatResponse response =
                chatService.ask(
                        "Summarize store 10001 health and recent activity",
                        "10001"
                );

        assertTrue(
                response.answer().contains("Store 10001")
        );

        assertTrue(
                response.answer().contains("ONLINE")
        );

        assertEquals(
                3,
                response.citations().size()
        );

        assertEquals(
                "STORE",
                response.citations().get(0).recordType()
        );

        assertEquals(
                "10001",
                response.citations().get(0).storeId()
        );
    }

    @Test
    void shouldReturnOfflinePumpStoresAndIncidentCitations() {

        Store store = new Store(
                "10004",
                "Speedway",
                "OFFLINE",
                12,
                0,
                12,
                true,
                "2026-03-02T07:59:44.300Z",
                new StoreAddress("OH", "Columbus"),
                "39.9612",
                "-82.9988",
                5
        );

        Incident incident = new Incident(
                "INC-10004-0001",
                "10004",
                "2026-03-02T07:40:00.000Z",
                "HIGH",
                "NETWORK",
                "Network outage",
                "OPEN"
        );

        RetrievedContext context =
                new RetrievedContext(
                        List.of(store),
                        List.of(incident),
                        List.of()
                );

        String question =
                "Which stores have the highest offline pumps "
                        + "and what incidents are associated with them?";

        when(retrievalService.retrieve(
                question,
                null
        )).thenReturn(context);

        when(contextAssembler.assemble(context))
                .thenReturn("assembled context");

        ChatResponse response =
                chatService.ask(
                        question,
                        null
                );

        assertTrue(
                response.answer().contains("10004")
        );

        assertTrue(
                response.answer().contains("12 offline pumps")
        );

        assertTrue(
                response.answer().contains("INC-10004-0001")
        );

        assertEquals(
                2,
                response.citations().size()
        );
    }

    @Test
    void shouldReturnNoCitationsWhenTankDataIsUnavailable() {

        ChatResponse response =
                chatService.ask(
                        "Any stores with low tank levels that look like runout risk?",
                        null
                );

        assertTrue(
                response.answer()
                        .contains("does not contain tank-level data")
        );

        assertTrue(
                response.citations().isEmpty()
        );

        assertTrue(
                response.retrievedContextSummary()
                        .contains("No relevant context")
        );
    }
}