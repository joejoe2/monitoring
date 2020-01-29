<?php
 $user="web";
 $pass="a43235167";
 $DBname="data";
 $target=$_GET['target'];

 header("Access-Control-Allow-Origin:*");

 $link=new MySQLi("localhost",$GLOBALS['user'],$GLOBALS['pass'],$DBname);
 if($link->connect_error){ echo "error!";}
 
 $target=json_decode($target);
 $r=array();

 for ($i=0; $i < count($target); $i++) { 
     # code...
     $t=$target[$i];
     $result=$link->query("SELECT * from "."$t"." ORDER BY time DESC"); 
     $r1=$result->fetch_row();
     array_push($r,$r1);
 }
 
 echo json_encode($r);
?>