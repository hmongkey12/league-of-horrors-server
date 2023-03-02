package com.serializers;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SerializableAbilityEntity implements Serializable {
    private String abilityName;
    private float abilityStart = 0;
    private float abilityEnd = 0;
    private float cooldownEnd = 0;
    private float cooldownStart = 0;
    private float damage = 0;
    int xPos = 0;
    int yPos = 0;
    int width = 0;
    int height = 0;
}
