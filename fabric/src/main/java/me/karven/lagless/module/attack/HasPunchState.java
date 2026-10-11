package me.karven.lagless.module.attack;

public interface HasPunchState {

    void setPunchState$lagless(final FabricAttackModule.PunchState punchState);
    FabricAttackModule.PunchState getPunchState$lagless();
}
