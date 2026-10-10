package me.karven.lagless.module.attack;

public interface HasPunchState {

    void setPunchState$lagless(final AttackModule.PunchState punchState);
    AttackModule.PunchState getPunchState$lagless();
}
