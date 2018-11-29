package Ex03.Manager;

import Ex03.GameLobby.Room;

import java.util.*;

// Manages open lobby game rooms
public class LobbyRoomsManager {

    private final Set<Room> m_AvailableRooms;

    public LobbyRoomsManager() {
        m_AvailableRooms = new HashSet<>();
    }

    public void AddRoom(Room i_LobbyRoom) {
        m_AvailableRooms.add(i_LobbyRoom);
    }

    public void RemoveRoom(String i_RoomName) {

        for(Room room : m_AvailableRooms){
            if(room.GetRoomName().equals(i_RoomName)){
                m_AvailableRooms.remove(room);
                break;
            }
        }
    }

    public Set<Room> GetLobbyRooms() {
        return Collections.unmodifiableSet(m_AvailableRooms);
    }

    public boolean IsRoomExist(String i_RoomName) {
        boolean isRoomNameExist=false;

        for(Room room: m_AvailableRooms) {
            if(room.GetRoomName().equals(i_RoomName)){
                isRoomNameExist=true;
                break;
            }
        }

        return isRoomNameExist;
    }

    public Room GetRoomByName(String i_RoomName){
        Room roomToReturn=null;

        for(Room room : m_AvailableRooms){
            if (room.GetRoomName().equals(i_RoomName))
            {
                roomToReturn=room;
                break;
            }
        }
        return  roomToReturn;
    }
}
