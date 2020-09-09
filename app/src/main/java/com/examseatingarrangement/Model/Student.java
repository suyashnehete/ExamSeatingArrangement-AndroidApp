package com.examseatingarrangement.Model;

import java.util.Date;

public class Student {
    String name, enrollment, roll, seatno, phone, email, classno, benchno;
    int month, date, year;

    public Student() {
    }

    public Student(String name, String enrollment, String roll, String seatno, String phone, String email, String classno, String benchno, int month, int date, int year) {
        this.name = name;
        this.enrollment = enrollment;
        this.roll = roll;
        this.seatno = seatno;
        this.phone = phone;
        this.email = email;
        this.classno = classno;
        this.benchno = benchno;
        this.month = month;
        this.date = date;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getSeatno() {
        return seatno;
    }

    public void setSeatno(String seatno) {
        this.seatno = seatno;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClassno() {
        return classno;
    }

    public void setClassno(String classno) {
        this.classno = classno;
    }

    public String getBenchno() {
        return benchno;
    }

    public void setBenchno(String benchno) {
        this.benchno = benchno;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
