package com.example.udpserver.models;

import com.example.udpserver.enums.FacingDirection;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
public class HeroEntity {
    private String heroName;
    private int xPos = 0;
    private int yPos = 0;
    private int width = 100;
    private int height = 200;
    private boolean isAttacking = false;
    private boolean isMoving = false;
    private List<AbilityEntity> abilities;
    private String id;
    private boolean isFalling = false;
    private boolean isJumping = false;
    private int health = 1000;
    private int movingStart = 0;
    private int movingEnd = 0;
    private FacingDirection facingDirection = FacingDirection.NONE;
}
