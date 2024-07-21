package com.mpesa.mpesab2c.repository;

import com.mpesa.mpesab2c.entities.GwRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GwRequestRepository extends JpaRepository<GwRequest, UUID> {

    GwRequest findByOriginatorConversationIDOrConversationID(String originatorConversationID, String conversationID);

    List<GwRequest> findAllByStatus(String status);
}
