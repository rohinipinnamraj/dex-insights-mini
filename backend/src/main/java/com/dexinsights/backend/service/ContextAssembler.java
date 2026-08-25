package com.dexinsights.backend.service;

import com.dexinsights.backend.dto.RetrievedContext;
import com.dexinsights.backend.model.Incident;
import com.dexinsights.backend.model.Store;
import com.dexinsights.backend.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class ContextAssembler {

    public String assemble(RetrievedContext context) {

        StringBuilder builder = new StringBuilder();

        for (Store store : context.stores()) {
            builder.append("STORE: ")
                    .append(store.storeId())
                    .append(", status=")
                    .append(store.status())
                    .append(", totalPumps=")
                    .append(store.totalPumps())
                    .append(", activePumps=")
                    .append(store.activePumps())
                    .append(", offlinePumps=")
                    .append(store.offlinePumps())
                    .append(", anomalyCount=")
                    .append(store.anomalyCount())
                    .append("\n");
        }

        for (Incident incident : context.incidents()) {
            builder.append("INCIDENT: ")
                    .append(incident.incidentId())
                    .append(", storeId=")
                    .append(incident.storeId())
                    .append(", severity=")
                    .append(incident.severity())
                    .append(", category=")
                    .append(incident.category())
                    .append(", status=")
                    .append(incident.status())
                    .append(", description=")
                    .append(incident.description())
                    .append(", timestamp=")
                    .append(incident.timestamp())
                    .append("\n");
        }

        for (Transaction transaction : context.transactions()) {
            builder.append("TRANSACTION: ")
                    .append(transaction.transactionId())
                    .append(", storeId=")
                    .append(transaction.storeId())
                    .append(", gradeName=")
                    .append(transaction.gradeName())
                    .append(", amount=")
                    .append(transaction.transactionAmnt())
                    .append(", volume=")
                    .append(transaction.volume())
                    .append(", startTime=")
                    .append(transaction.transactionStartTime())
                    .append("\n");
        }

        return builder.toString();
    }
}