package Ex03.GameEngine;


import javafx.scene.layout.AnchorPane;
import jdk.nashorn.internal.objects.annotations.Getter;

import java.io.Serializable;

public class PlayerStatics implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private int m_Hits;
    private int m_Score;    // Number of hits
    private int m_Misses;     // Number of failed attacks
    private int m_TurnsTaken;
    private long m_TotalTimePlayed;

    public PlayerStatics() {
        m_Misses = 0;
        m_Score = 0;
        m_TurnsTaken = 0;
        m_TotalTimePlayed = 0;
        m_Hits = 0;
    }

    public int GetHits() {
        return m_Hits;
    }

    public void IncreaseHits() {
        m_Hits++;
    }

    @Getter
    public int GetScore() {
        return m_Score;
    }

    public void IncreaseScoreWhenHit() {
        this.m_Score++;
    }

    public void IncreaseScoreWhenDrown(int i_ShipsScore) {
        this.m_Score += i_ShipsScore;
    }

    public void AddToTimePlayed(long i_TurnTime) {  // Time is given in seconds
        m_TotalTimePlayed += i_TurnTime;
    }

    @Getter
    public long GetTotalTimePlayed() {
        return m_TotalTimePlayed;
    }

    @Getter
    public int GetTurnsTaken() {

        return m_TurnsTaken;
    }

    @Getter
    public int GetNumberOfMisses() {

        return m_Misses;
    }

    @Getter
    public long GetAverageTurnTime() {
        if (m_TurnsTaken != 0)
            return m_TotalTimePlayed / m_TurnsTaken;
        else {
            return 0;
        }
    }



    public void IncreaseMissCount() {
        m_Misses++;
    }

    public void SetHits(int m_Hits) {
        this.m_Hits = m_Hits;
    }

    public void SetScore(int m_Score) {
        this.m_Score = m_Score;
    }

    public void SetMisses(int m_Misses) {
        this.m_Misses = m_Misses;
    }

    public void SetTurnsTaken(int m_TurnsTaken) {
        this.m_TurnsTaken = m_TurnsTaken;
    }

    public void SetTotalTimePlayed(long m_TotalTimePlayed) {
        this.m_TotalTimePlayed = m_TotalTimePlayed;
    }

    public void IncreaseTurnCount() {
        m_TurnsTaken++;
    }

    @Override
    public PlayerStatics clone() {
        PlayerStatics newStatics = new PlayerStatics();
        newStatics.SetHits(m_Hits);
        newStatics.SetMisses(m_Misses);
        newStatics.SetScore(m_Score);
        newStatics.SetTotalTimePlayed(m_TotalTimePlayed);
        newStatics.SetTurnsTaken(m_TurnsTaken);
        return newStatics;
    }
}