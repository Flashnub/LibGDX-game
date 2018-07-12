<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tileset2" tilewidth="30" tileheight="30" tilecount="6" columns="2">
 <image source="tileset2.png" width="60" height="90"/>
 <tile id="0">
  <properties>
   <property name="Impassable" type="bool" value="true"/>
  </properties>
 </tile>
 <tile id="1">
  <properties>
   <property name="Impassable" type="bool" value="false"/>
  </properties>
 </tile>
 <tile id="3">
  <properties>
   <property name="EntityType" value="Enemy"/>
   <property name="Impassable" type="bool" value="false"/>
   <property name="SpawnPoint" value="BasicEnemy"/>
  </properties>
 </tile>
 <tile id="4">
  <properties>
   <property name="EntityType" value="Player"/>
   <property name="Impassable" type="bool" value="false"/>
   <property name="SpawnPoint" value="Player"/>
  </properties>
 </tile>
</tileset>
