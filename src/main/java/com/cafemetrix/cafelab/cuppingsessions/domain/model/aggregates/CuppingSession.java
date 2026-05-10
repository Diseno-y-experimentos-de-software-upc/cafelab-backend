package com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.CreateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.UpdateCuppingSessionCommand;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
public class CuppingSession extends AuditableAbstractAggregateRoot<CuppingSession> {

    @Getter
    @Column(name = "profile_id", nullable = false)
    private Long userId;

    @Getter
    @Column(nullable = false, length = 255)
    private String name;

    @Getter
    @Column(nullable = false, length = 255)
    private String origin;

    @Getter
    @Column(nullable = false, length = 255)
    private String variety;

    @Getter
    @Column(nullable = false, length = 120)
    private String processing;

    @Getter
    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Getter
    @Column(nullable = false)
    private boolean favorite;

    @Getter
    @Lob
    @Column(name = "results_json", columnDefinition = "LONGTEXT")
    private String resultsJson;

    @Getter
    @Column(name = "roast_style_notes", columnDefinition = "TEXT")
    private String roastStyleNotes;

    public CuppingSession() {}

    public CuppingSession(CreateCuppingSessionCommand c) {
        this.userId = c.userId();
        this.name = c.name().trim();
        this.origin = c.origin().trim();
        this.variety = c.variety().trim();
        this.processing = c.processing().trim();
        this.sessionDate = c.sessionDate();
        this.favorite = c.favorite();
        this.resultsJson = blankToNull(c.resultsJson());
        this.roastStyleNotes = blankToNull(c.roastStyleNotes());
    }

    public void applyUpdate(UpdateCuppingSessionCommand c) {
        this.name = c.name().trim();
        this.origin = c.origin().trim();
        this.variety = c.variety().trim();
        this.processing = c.processing().trim();
        this.sessionDate = c.sessionDate();
        this.favorite = c.favorite();
        this.resultsJson = blankToNull(c.resultsJson());
        this.roastStyleNotes = blankToNull(c.roastStyleNotes());
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}
