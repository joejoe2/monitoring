<?php 
    $user="root";
    $pass="a43235167";
    $DBname="monitor_db";

    header("Access-Control-Allow-Origin:*");

    $link=new MySQLi("localhost",$GLOBALS['user'],$GLOBALS['pass'],$DBname);
    if($link->connect_error){ echo "error!";}
    $s="SELECT * from "."devices_cfg"."";
    $r=$link->query($s);
    $r=$r->fetch_all();
    $r=json_encode($r);
    echo $r;
?>