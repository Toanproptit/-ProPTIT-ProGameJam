package com.Trongtoan.ProjGame.logic;

import com.badlogic.gdx.math.Rectangle;
import com.Trongtoan.ProjGame.entities.Player;
import com.Trongtoan.ProjGame.entities.Monster;
import java.util.*;



public class MissionManager {
    private final Map<String, Mission> missionMap = new LinkedHashMap<>();
    private String currentMissionId = null;

    public void setMissionSequence(List<Mission> missionList) {
        missionMap.clear();
        for (Mission m : missionList) {
            missionMap.put(m.getId(), m);
        }
        if (!missionList.isEmpty()) {
            currentMissionId = missionList.get(0).getId();
        }
    }

    public Mission getCurrentMission() {
        return currentMissionId != null ? missionMap.get(currentMissionId) : null;
    }

    public void completeCurrentMission() {
        if (currentMissionId == null) return;
        missionMap.get(currentMissionId).complete();

        boolean found = false;
        for (String id : missionMap.keySet()) {
            if (found) {
                currentMissionId = id;
                return;
            }
            if (id.equals(currentMissionId)) {
                found = true;
            }
        }
        currentMissionId = null; // No next mission
    }

    public boolean hasMissionInProgress() {
        return currentMissionId != null;
    }

    public void resetAll() {
        for (Mission m : missionMap.values()) {
            m.reset();
        }
        if (!missionMap.isEmpty()) {
            currentMissionId = missionMap.keySet().iterator().next();
        }
    }

    public Collection<Mission> getAllMissions() {
        return missionMap.values();
    }

    public void updateMissions(Player player, List<Monster> monsters, Rectangle portalRect) {
        Mission current = getCurrentMission();
        if (current == null || current.isCompleted()) return;

        switch (current.getId()) {
            case "kill_doll_5":
                if (player.getDollsKilled() >= 5) {
                    completeCurrentMission();
                }
                break;

            case "reach_stats":
                boolean statOk = player.getMaxHp() >= 500 &&
                    player.getMaxMp() >= 500 &&
                    player.getBaseDamage() >= 50;
                if (statOk) {
                    completeCurrentMission();
                }
                break;
            case "kill_boar_30":
                if (player.getBoarsKilled() >= 30) {
                    completeCurrentMission();
                }
                break;

            case "reach_stats_map1":
                boolean statHigh = player.getMaxHp() >= 1000 &&
                    player.getMaxMp() >= 1000 &&
                    player.getBaseDamage() >= 100;
                if (statHigh) {
                    completeCurrentMission();
                }
                break;
            case "reach_stats_map3":
                boolean statHigh1 = player.getMaxHp() >= 2000 &&
                    player.getMaxMp() >= 2000 &&
                    player.getBaseDamage() >= 200;
                if (statHigh1) {
                    completeCurrentMission();
                }
                break;
            case "reach_stats_map4":
                boolean statHigh2 = player.getMaxHp() >= 3000 &&
                    player.getMaxMp() >= 3000 &&
                    player.getBaseDamage() >= 400;
                if (statHigh2) {
                    completeCurrentMission();
                }
                break;
            case "kill_golem_20":
                if (player.getGolemsKilled() >= 20) {
                    completeCurrentMission();
                }
                break;
            case "Kill_demonfly_20":
                if(player.getDemonflyKilled()>=20){
                    completeCurrentMission();
                }
            case "boss_defeat":
                boolean bossDead = monsters.stream()
                    .anyMatch(m -> m.getType().toLowerCase().contains("boss") && !m.isAlive());
                if (bossDead) {
                    completeCurrentMission();
                }
                break;
        }
    }
}





