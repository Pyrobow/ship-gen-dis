package com.generator.main.objects;

public class Pair<I , B> {
    private I first;
    private B second;

    public Pair(I first, B second){
        this.first = first;
        this.second = second;
    }

    public I first(){
        return  this.first;
    }

    public B second(){
        return this.second;
    }
}
