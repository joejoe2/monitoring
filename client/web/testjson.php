<?php
 $user="root";
 $pass="a43235167";
 $DBname="data";

 $target=$_GET['target'];

 $link=new MySQLi("localhost",$GLOBALS['user'],$GLOBALS['pass'],$DBname);
 if($link->connect_error){ echo "error!";}
 $result=$link->query("SELECT * from "."$target"." ORDER BY time DESC"); 
 $r1=$result->fetch_row();
 echo "".$r1[0].";".$r1[1].";".$r1[2].";".$r1[3]."";
?>