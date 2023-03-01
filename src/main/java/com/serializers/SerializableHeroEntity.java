package com.serializers;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class SerializableHeroEntity implements Serializable {
    private String heroName;
    private int xPos = 0;
    private int yPos = 0;
    private int width = 100;
    private int height = 200;
    private boolean isAttacking = false;
    private boolean isMoving = false;
    private List<SerializableAbilityEntity> abilities;
    private String id;
    private boolean isFalling = false;
    private boolean isJumping = false;
    private float jumpStart = 0;
    private int health = 1000;
    private float movingStart = 0;
    private float movingEnd = 0;
    private float attackStart = 0;
    private float attackEnd = 0;
    private String facingDirection = "none";
}
