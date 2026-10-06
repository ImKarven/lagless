package me.karven.lagless.module.attack;

public interface HasPunchState {

    void lagless$attack$setPunchState(final AttackModule.PunchState punchState);
    AttackModule.PunchState lagless$attack$getPunchState();
}
