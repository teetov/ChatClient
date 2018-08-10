package com.teetov.chat.client.prop;


import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "servers")
public class ServersStore {
    
    @XmlElement
    private List<ServerDescr> server;
    
    public List<ServerDescr> getServers() {
        return server;
    }
    
    public void setServers(List<ServerDescr> servers) {
        server = servers;
    }
    
}
