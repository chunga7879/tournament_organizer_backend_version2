package com.hairlesscat.app.match;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.util.TeamStatus;

import java.io.IOException;
import java.util.Map;

public class TeamStatusSerializer extends JsonSerializer<Map<Team, TeamStatus>> {
    @Override
    public void serialize(Map<Team, TeamStatus> teamStatusMap, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();

        for (Team team : teamStatusMap.keySet()) {
            gen.writeStartObject();
            gen.writeNumberField("team_id", team.getTeamId());
            gen.writeStringField("team_status", teamStatusMap.get(team).toString());
            gen.writeEndObject();
        }

        gen.writeEndArray();
    }
}
