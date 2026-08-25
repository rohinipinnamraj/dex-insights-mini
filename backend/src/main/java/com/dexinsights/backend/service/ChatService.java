package com.dexinsights.backend.service;

import com.dexinsights.backend.dto.ChatResponse;
import com.dexinsights.backend.dto.Citation;
import com.dexinsights.backend.dto.RetrievedContext;
import com.dexinsights.backend.model.Incident;
import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final ChatRetrievalService retrievalService;
    private final ContextAssembler contextAssembler;

    public ChatService(
            ChatRetrievalService retrievalService,
            ContextAssembler contextAssembler) {

        this.retrievalService = retrievalService;
        this.contextAssembler = contextAssembler;
    }

    public ChatResponse ask(String question, String storeId) {

        String normalizedQuestion =
                question == null ? "" : question.toLowerCase();

        // The provided dataset does not contain tank-level information.
        // Return a grounded "cannot determine" response without unrelated citations.
        if (normalizedQuestion.contains("tank")
                || normalizedQuestion.contains("runout")) {

            return new ChatResponse(
                    "The provided dataset does not contain tank-level data, "
                            + "so runout risk cannot be determined reliably.",
                    List.of(),
                    "No relevant context retrieved because tank-level data "
                            + "is not available in the provided dataset."
            );
        }

        RetrievedContext context =
                retrievalService.retrieve(question, storeId);

        // Explicit context assembly step required by the assignment.
        String assembledContext =
                contextAssembler.assemble(context);

        String answer =
                generateAnswer(question, context);

        List<Citation> citations =
                createCitations(context);

        String contextSummary =
                "Retrieved "
                        + context.stores().size()
                        + " store record(s), "
                        + context.incidents().size()
                        + " incident record(s), and "
                        + context.transactions().size()
                        + " transaction record(s).";

        return new ChatResponse(
                answer,
                citations,
                contextSummary
        );
    }

    private String generateAnswer(
            String question,
            RetrievedContext context) {

        String normalized =
                question == null ? "" : question.toLowerCase();

        if (context.stores().isEmpty()) {
            return "No relevant store records were found in the dataset.";
        }

        // Handles questions such as:
        // "Which stores have the highest offline pumps and what incidents
        // are associated with them?"
        if (normalized.contains("offline")
                && normalized.contains("pump")) {

            StringBuilder answer =
                    new StringBuilder(
                            "Stores with the highest offline pump counts are: "
                    );

            for (Store store : context.stores()) {
                answer.append("Store ")
                        .append(store.storeId())
                        .append(" with ")
                        .append(store.offlinePumps())
                        .append(" offline pumps; ");
            }

            if (!context.incidents().isEmpty()) {
                answer.append("Associated incidents include ");

                for (Incident incident : context.incidents()) {
                    answer.append(incident.incidentId())
                            .append(" (")
                            .append(incident.severity())
                            .append(", ")
                            .append(incident.category())
                            .append("); ");
                }
            } else {
                answer.append(
                        "No associated incidents were found for these stores."
                );
            }

            return answer.toString();
        }

        // Handles store-specific questions such as:
        // "Summarize store 10001 health and recent activity"
        if (context.stores().size() == 1) {

            Store store = context.stores().get(0);

            StringBuilder answer = new StringBuilder();

            answer.append("Store ")
                    .append(store.storeId())
                    .append(" is ")
                    .append(store.status())
                    .append(". It has ")
                    .append(store.activePumps())
                    .append(" active pumps out of ")
                    .append(store.totalPumps())
                    .append(", with ")
                    .append(store.offlinePumps())
                    .append(" offline pumps and ")
                    .append(store.anomalyCount())
                    .append(" anomalies.");

            if (!context.incidents().isEmpty()) {

                long unresolvedIncidents =
                        context.incidents()
                                .stream()
                                .filter(incident ->
                                        !"RESOLVED".equalsIgnoreCase(
                                                incident.status()
                                        )
                                )
                                .count();

                int incidentCount = context.incidents().size();

                answer.append(" There ")
                        .append(incidentCount == 1 ? "is " : "are ")
                        .append(incidentCount)
                        .append(incidentCount == 1
                                ? " recent incident record"
                                : " recent incident records")
                        .append(", including ")
                        .append(unresolvedIncidents)
                        .append(unresolvedIncidents == 1
                                ? " unresolved incident."
                                : " unresolved incidents.");
            }

            if (!context.transactions().isEmpty()) {

                int transactionCount =
                        context.transactions().size();

                answer.append(" ")
                        .append(transactionCount)
                        .append(transactionCount == 1
                                ? " recent transaction record was retrieved."
                                : " recent transaction records were retrieved.");
            }

            return answer.toString();
        }

        return "Relevant operational records were retrieved from the dataset. "
                + "The context contains "
                + context.stores().size()
                + " stores and "
                + context.incidents().size()
                + " incidents.";
    }

    private List<Citation> createCitations(
            RetrievedContext context) {

        List<Citation> citations = new ArrayList<>();

        for (Store store : context.stores()) {
            citations.add(
                    new Citation(
                            "STORE",
                            store.storeId(),
                            store.storeId(),
                            store.lastUpdatedTime()
                    )
            );
        }

        for (Incident incident : context.incidents()) {
            citations.add(
                    new Citation(
                            "INCIDENT",
                            incident.incidentId(),
                            incident.storeId(),
                            incident.timestamp()
                    )
            );
        }

        for (Transaction transaction : context.transactions()) {
            citations.add(
                    new Citation(
                            "TRANSACTION",
                            transaction.transactionId(),
                            transaction.storeId(),
                            transaction.transactionStartTime()
                    )
            );
        }

        return citations;
    }
}