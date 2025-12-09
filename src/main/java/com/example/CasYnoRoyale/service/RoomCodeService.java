package com.example.CasYnoRoyale.service;


import org.hashids.Hashids;
import org.springframework.stereotype.Service;

@Service
public class RoomCodeService {

    private static final String SALT = "MonSelSecretPourLeCasin";
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Hashids hashids;

    public RoomCodeService() {
        this.hashids = new Hashids(SALT, 6, ALPHABET);
    }

    public String generateCode(Long roomId) {
        return hashids.encode(roomId);
    }

    public Long decodeRoomId(String code) {
        if (code == null) return null;
        String cleanCode = code.trim().toUpperCase().replaceAll("\"","");
        long[] ids = hashids.decode(cleanCode);
        return ids.length > 0 ? ids[0] : null;
    }
    public static void main(String[] args){
        RoomCodeService roomCodeService = new RoomCodeService();
        System.out.println(roomCodeService.generateCode(147L));
        System.out.println("L0V773");
        System.out.println(roomCodeService.decodeRoomId("L0V773"));
    }
}
