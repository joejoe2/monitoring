<?php
$user="root";
$pass="a43235167";
$DBname="monitor_db";

$link=new MySQLi("localhost",$GLOBALS['user'],$GLOBALS['pass'],$DBname);
if($link->connect_error){ echo "error!";}
$s="TRUNCATE devices_cfg";
$link->query($s);
?>