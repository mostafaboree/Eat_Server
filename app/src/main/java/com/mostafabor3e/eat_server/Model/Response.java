package com.mostafabor3e.eat_server.Model;

import java.util.List;

public class Response {
    private  int multicast_id;
    private  int success;
    private  int failure;
    private  int conanicen_id;
    private List<Ruselt>ruselts;

    public Response(int multicast_id, int success, int failure, int conanicen_id, List<Ruselt> ruselts) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.conanicen_id = conanicen_id;
        this.ruselts = ruselts;
    }
}
