package Ex03.GameEngine;

public class BoardPointStatus {

    public enum PictureType {
        NONE, LEFT_HEAD, RIGHT_HEAD, BODY, FULL, DOWN_HEAD, UP_HEAD, CORNER_BODY, MINE
    }

    public enum PictureStatus {
        NONE, ATTACKED, DESTROYED
    }

    private PictureType m_PointPicture;
    private PictureStatus m_PointPictureStatus;

    public BoardPointStatus() {
        m_PointPictureStatus=PictureStatus.NONE;
        m_PointPicture=PictureType.NONE;
    }

    public BoardPointStatus(PictureType i_PictureType, PictureStatus i_PictureStatus) {
        m_PointPicture = i_PictureType;
        m_PointPictureStatus = i_PictureStatus;
    }

    public PictureStatus GetPointPictureStatus() {
        return m_PointPictureStatus;
    }

    public void SetPointPictureStatus(PictureStatus m_PointPictureStatus) {
        this.m_PointPictureStatus = m_PointPictureStatus;
    }

    public PictureType GetPointPicture() {

        return m_PointPicture;
    }

    public void SetPointPicture(PictureType m_PointPicture) {
        this.m_PointPicture = m_PointPicture;
    }
}
