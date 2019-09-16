<?php
$user="root";
$pass="a43235167";
$DBname="monitor_db";
$table="devices_config";

$target=$_POST['target'];
$devicesid=$_POST['devicesid'];
$obj=$_POST['obj'];
$link=new MySQLi("localhost",$GLOBALS['user'],$GLOBALS['pass'],$DBname);
if($link->connect_error){ echo "error!";}
$s="INSERT INTO "."$table"."(target,id,value) VALUES('"."$target"."','"."$devicesid"."','"."$obj"."')";
$link->query($s);
?>