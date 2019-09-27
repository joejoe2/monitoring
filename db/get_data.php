<?php
$user="root";
$pass="a43235167";
$DBname="monitor_db";

$target=$_GET['target'];
$year=$_GET['year'];
$month=$_GET['month'];
$day=$_GET['day'];

$link=new MySQLi("localhost",$GLOBALS['user'],$GLOBALS['pass'],$DBname);
if($link->connect_error){ echo "error!";}

if ($day!="all") {
    $s="SELECT * from "."$target"." where year = '"."$year"."' and month = '"."$month"."' and day = '"."$day"."'";
    $result=$link->query($s);
    //die($link->error);
    $result=$result->fetch_all();
    echo json_encode($result);
    /*
    for ($i=0; $i <sizeof($result) ; $i++) { 
        # code...
        echo "<br>".var_dump($result[$i]);
    }*/
}else{

}
?>