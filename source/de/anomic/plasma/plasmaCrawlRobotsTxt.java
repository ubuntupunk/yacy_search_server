//plasmaCrawlRobotsTxt.java 
//-------------------------------------
//part of YACY
//(C) by Michael Peter Christen; mc@anomic.de
//first published on http://www.anomic.de
//Frankfurt, Germany, 2004
//
//This file ist contributed by Martin Thelian
//last major change: $LastChangedDate$ by $LastChangedBy$
//Revision: $LastChangedRevision$
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
//Using this software in any meaning (reading, learning, copying, compiling,
//running) means that you agree that the Author(s) is (are) not responsible
//for cost, loss of data or any harm that may be caused directly or indirectly
//by usage of this softare or this documentation. The usage of this software
//is on your own risk. The installation and usage (starting/running) of this
//software may allow other people or application to access your computer and
//any attached devices and is highly dependent on the configuration of the
//software which must be done by the user of the software; the author(s) is
//(are) also not responsible for proper configuration and usage of the
//software, even if provoked by documentation provided together with
//the software.
//
//Any changes to this file according to the GPL as documented in the file
//gpl.txt aside this file in the shipment you received can be done to the
//lines that follows this copyright notice here, but changes must not be
//done inside the copyright notive above. A re-distribution must contain
//the intact and unchanged copyright notice.
//Contributions and changes to the program code must be marked as such.


package de.anomic.plasma;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import de.anomic.kelondro.kelondroDyn;
import de.anomic.kelondro.kelondroException;
import de.anomic.kelondro.kelondroMap;

public class plasmaCrawlRobotsTxt {
    
    public static final String ROBOTS_DB_PATH_SEPARATOR = ";";    
    
    kelondroMap robotsTable;
    private final File robotsTableFile;
    private int bufferkb;
    private long preloadTime;
    
    public plasmaCrawlRobotsTxt(File robotsTableFile, int bufferkb, long preloadTime) {
        this.robotsTableFile = robotsTableFile;
        this.bufferkb = bufferkb;
        this.preloadTime = preloadTime;
        robotsTableFile.getParentFile().mkdirs();
        robotsTable = new kelondroMap(kelondroDyn.open(robotsTableFile, bufferkb * 1024, preloadTime, 256, 512, '_', true, false));
    }
    
    public int cacheNodeChunkSize() {
        return robotsTable.cacheNodeChunkSize();
    }    
    
    public int cacheObjectChunkSize() {
        return robotsTable.cacheObjectChunkSize();
    }    
    
    public int[] cacheNodeStatus() {
        return robotsTable.cacheNodeStatus();
    }    
    
    public long[] cacheObjectStatus() {
        return robotsTable.cacheObjectStatus();
    }
    
    private void resetDatabase() {
        // deletes the robots.txt database and creates a new one
        if (robotsTable != null) try {
            robotsTable.close();
        } catch (IOException e) {}
        if (!(robotsTableFile.delete())) throw new RuntimeException("cannot delete robots.txt database");
        robotsTableFile.getParentFile().mkdirs();
        robotsTable = new kelondroMap(kelondroDyn.open(robotsTableFile, this.bufferkb, preloadTime, 256, 512, '_', true, false));
    }
    
    public void close() {
        try {
            robotsTable.close();
        } catch (IOException e) {}
    }
    
    public int size() {
        return robotsTable.size();
    }    
    
    public void removeEntry(String hostName) {
        try {
            robotsTable.remove(hostName.toLowerCase());
        } catch (IOException e) {
        	
        } catch (kelondroException e) {
    			resetDatabase();
        }
    }
    
    public Entry getEntry(String hostName) {
        try {
            Map record = robotsTable.get(hostName);
            if (record == null) return null;
            return new Entry(hostName, record);
        } catch (IOException e) {
            return null;
        } catch (kelondroException e) {
        		resetDatabase();
        		return null;
        }
    }    
    
    public Entry addEntry(String hostName, ArrayList disallowPathList, Date loadedDate, Date modDate, String eTag) {
        Entry entry = new Entry(hostName,disallowPathList,loadedDate,modDate,eTag);
        addEntry(entry);
        return entry;
    }
    
    public String addEntry(Entry entry) {
        // writes a new page and returns key
        try {
            robotsTable.set(entry.hostName,entry.mem);
            return entry.hostName;
        } catch (IOException e) {
            return null;
        }
    }    
    
    public class Entry {
        public static final String DISALLOW_PATH_LIST = "disallow";
        public static final String LOADED_DATE = "date";
        public static final String MOD_DATE = "modDate";
        public static final String ETAG = "etag";
        
        // this is a simple record structure that hold all properties of a single crawl start
        private Map mem;
        private LinkedList disallowPathList;
        private String hostName;
        
        public Entry(String hostName, Map mem) {
            this.hostName = hostName.toLowerCase();
            this.mem = mem; 
            
            if (this.mem.containsKey(DISALLOW_PATH_LIST)) {
                this.disallowPathList = new LinkedList();
                String csPl = (String) this.mem.get(DISALLOW_PATH_LIST);
                if (csPl.length() > 0){
                    String[] pathArray = csPl.split(ROBOTS_DB_PATH_SEPARATOR);
                    if ((pathArray != null)&&(pathArray.length > 0)) {
                        this.disallowPathList.addAll(Arrays.asList(pathArray));
                    }
                }
            } else {
                this.disallowPathList = new LinkedList();
            }
        }  
        
        public Entry(
                String hostName, 
                ArrayList disallowPathList, 
                Date loadedDate,
                Date modDate,
                String eTag) {
            if ((hostName == null) || (hostName.length() == 0)) throw new IllegalArgumentException();
            
            this.hostName = hostName.trim().toLowerCase();
            this.disallowPathList = new LinkedList();
            
            this.mem = new HashMap();
            if (loadedDate != null) this.mem.put(LOADED_DATE,Long.toString(loadedDate.getTime()));
            if (modDate != null) this.mem.put(MOD_DATE,Long.toString(modDate.getTime()));
            if (eTag != null) this.mem.put(ETAG,eTag);
            
            if ((disallowPathList != null)&&(disallowPathList.size()>0)) {
                this.disallowPathList.addAll(disallowPathList);
                
                StringBuffer pathListStr = new StringBuffer();
                for (int i=0; i<disallowPathList.size();i++) {
                    pathListStr.append(disallowPathList.get(i))
                               .append(ROBOTS_DB_PATH_SEPARATOR);
                }
                this.mem.put(DISALLOW_PATH_LIST,pathListStr.substring(0,pathListStr.length()-1));
            }
        }
        
        public String toString() {
            StringBuffer str = new StringBuffer();
            str.append((this.hostName==null)?"null":this.hostName)
               .append(": ");
            
            if (this.mem != null) {     
                str.append(this.mem.toString());
            } 
            
            return str.toString();
        }    
        
        public Date getLoadedDate() {
            if (this.mem.containsKey(LOADED_DATE)) {
                return new Date(Long.valueOf((String) this.mem.get(LOADED_DATE)).longValue());
            }
            return null;
        }
        
        public void setLoadedDate(Date newLoadedDate) {
            if (newLoadedDate != null) {
                this.mem.put(LOADED_DATE,Long.toString(newLoadedDate.getTime()));
            }
        }
        
        public Date getModDate() {
            if (this.mem.containsKey(MOD_DATE)) {
                return new Date(Long.valueOf((String) this.mem.get(MOD_DATE)).longValue());
            }
            return null;
        }        
        
        public String getETag() {
            if (this.mem.containsKey(ETAG)) {
                return (String) this.mem.get(ETAG);
            }
            return null;
        }          
        
        public boolean isDisallowed(String path) {
            if ((this.mem == null) || (this.disallowPathList.size() == 0)) return false;   
            
            // if the path is null or empty we set it to /
            if ((path == null) || (path.length() == 0)) path = "/";            
            // escaping all occurences of ; because this char is used as special char in the Robots DB
            else  path = path.replaceAll(ROBOTS_DB_PATH_SEPARATOR,"%3B");
            
            
            Iterator pathIter = this.disallowPathList.iterator();
            while (pathIter.hasNext()) {
                String nextPath = (String) pathIter.next();
                // allow rule
                if (nextPath.startsWith("!") && nextPath.length() > 1 && path.startsWith(nextPath.substring(1))) {
                    return false;
                }
                    
                // disallow rule
                if (path.startsWith(nextPath)) {
                    return true;
                }
            }
            return false;
        }
    
    }
    
    
}
