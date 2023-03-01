package com.example.udpserver.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AbilityEntity {
    private String abilityName;
    private float abilityStart = 0;
    private float abilityEnd = 0;
    private float cooldownEnd = 0;
    private float cooldownStart = 0;
    int xPos = 0;
    int yPos = 0;
    int width = 0;
    int height = 0;
}
