package com.hairlesscat.app.match;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.util.TeamStatus;

import java.util.HashMap;
import java.util.Map;

public class TeamStatusDeserializer extends JsonDeserializer<Map<Team, TeamStatus>> {
    @Override
    public Map<Team, TeamStatus> deserialize(JsonParser p, DeserializationContext ctx) {
        return new HashMap<>();
    }
}

