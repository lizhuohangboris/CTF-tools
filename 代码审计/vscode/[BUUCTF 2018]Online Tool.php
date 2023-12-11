<?php


    $host = $_GET['host'];
    $host = escapeshellarg($host);
    echo $host."<br>";
    $host = escapeshellcmd($host);
    echo $host."<br>";
    highlight_file(__FILE__);
