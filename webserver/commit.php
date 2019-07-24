<?php
 $user="root";
 $pass="a43235167";
 $DBname="data";
 
 $target=$_POST['target'];
 $gatewayid=$_POST['gatewayid'];
 $status=$_POST['status'];
 $time=$_POST['time'];
 $obj=$_POST['obj'];

 $link=new MySQLi("localhost",$GLOBALS['user'],$GLOBALS['pass'],$DBname);
 if($link->connect_error){ echo "error!";}
 $s="INSERT INTO "."$target"."(id,status,time,value) VALUES('"."$gatewayid"."','"."$status"."','"."$time"."','"."$obj"."')";
//  $link->query("set names utf8");
  $link->query($s);
  
  $c=$link->query("SELECT COUNT(*) FROM "."$target"."")->fetch_row();
  if ($c[0]>5) {
    $result=$link->query("SELECT * from "."$target"." ORDER BY time"); 
    $r1=$result->fetch_row();
    $link->query("DELETE FROM "."$target"." WHERE time='"."$r1[2]"."'");
    //echo "del".$r1[2];
  }
  //echo "$c[0]";
?>