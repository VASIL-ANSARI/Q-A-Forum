package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;

public class Tags extends AppCompatActivity {

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        gridView=findViewById(R.id.grid_layout);

        getSupportActionBar().hide();


        ArrayList<Tag> courseModelArrayList = new ArrayList<Tag>();
        courseModelArrayList.add(new Tag(".net",".NET is a developer platform with tools and libraries for building any type of app, including web, mobile, desktop, games, IoT, cloud, and microservices." ));
        courseModelArrayList.add(new Tag("android"," Android is a mobile operating system based on a modified version of the Linux kernel and other open source software, designed primarily for touchscreen mobile devices" ));
        courseModelArrayList.add(new Tag("asp.net","ASP.NET is a web application framework developed and marketed by Microsoft to allow programmers to build dynamic web sites. It allows you to use a full featured programming language such as C# or VB.NET to build web applications easily." ));
        courseModelArrayList.add(new Tag("C","C Language is a structure-oriented, middle-level programming language mostly used to develop low-level applications." ));
        courseModelArrayList.add(new Tag("C++","C++ is a general purpose, object-oriented, middle-level programming language and is an extension of C language, which makes it possible to code C++ in a “C style”. In some situations, coding can be done in either format, making C++ an example of a hybrid language." ));
        courseModelArrayList.add(new Tag("C#","Pronounced C-sharp (not C-hashtag), C# is a multi-paradigm programming language that features strong typing, imperative, declarative, functional, generic, object-oriented and component-oriented disciplines."));
        courseModelArrayList.add(new Tag("CSS","CSS is the language we use to style an HTML document.\n" +
                "\n" +
                "CSS describes how HTML elements should be displayed." ));
        courseModelArrayList.add(new Tag("HTML", "HTML is the standard markup language used to create web pages; it ensures proper formatting of text and images (using tags) so that Internet browsers can display them in the ways they were intended to look."));
        courseModelArrayList.add(new Tag("IOS","iOS (formerly iPhone OS) is a mobile operating system created and developed by Apple Inc. exclusively for its hardware.It is the operating system that powers many of the company's mobile" ));
        courseModelArrayList.add(new Tag("IPhone","iPhone from Apple, that was first released in June 29,2007, has created a wave in the smartphone industry with its stunning and incredible features and specifications. " ));
        courseModelArrayList.add(new Tag("Python","Python is an interpreted high-level general-purpose programming language.Its design philosophy emphasizes code readability with its use of significant indentation."));
        courseModelArrayList.add(new Tag("Java","Java is a general-purpose, object-oriented, high-level programming language with several features that make it ideal for web-based development."));
        courseModelArrayList.add(new Tag("JQuery","jQuery is a JavaScript Library. jQuery greatly simplifies JavaScript programming. jQuery is easy to learn." ));
        courseModelArrayList.add(new Tag("JavaScript","JavaScript is a client-side programming language that runs inside a client browser and processes commands on a computer rather than a server. It is commonly placed into an HTML or ASP file. Despite its name, JavaScript is not related to Java." ));
        courseModelArrayList.add(new Tag("MySQL", "MySQL Database Service with HeatWave. MySQL Database Service is a fully managed database service to deploy cloud-native applications."));
        courseModelArrayList.add(new Tag("Objective C", "Objective-C is a simple, general-purpose and object-oriented language. It uses a system of message passing borrowed from the language Smalltalk; when an object in Objective-C is sent a message, it can choose to ignore or forward to another object, rather than return a value."));
        courseModelArrayList.add(new Tag("PHP","PHP is an open-source scripting language designed for creating dynamic web pages that effectively work with databases. It is also used as a general-purpose programming language."));
        courseModelArrayList.add(new Tag("Ruby","Ruby is an open-sourced, object-oriented scripting language that can be used independently or as part of the Ruby on Rails web framework." ));
        courseModelArrayList.add(new Tag("Ruby on Rails", "Ruby is an open-sourced, object-oriented scripting language that can be used independently or as part of the Ruby on Rails web framework."));
        courseModelArrayList.add(new Tag("SQL","SQL is a database query language (not a development language) that allows for adding, accessing and managing content in a database. It is the language that allows programmers to perform the common acronym CRUD (Create; Read; Update; Delete) within a database." ));

        TagsAdapter adapter = new TagsAdapter(Tags.this, courseModelArrayList);
        gridView.setAdapter(adapter);
    }
}