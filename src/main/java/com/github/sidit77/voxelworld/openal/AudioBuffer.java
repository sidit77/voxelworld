package com.github.sidit77.voxelworld.openal;

import org.lwjgl.openal.AL10;

import java.nio.ByteBuffer;

public class AudioBuffer {

    /**
     * A simple wrapper around an OpenAL buffer object.
     */

    private int id;

    public AudioBuffer(int format, ByteBuffer data, int frequency){
        id = AL10.alGenBuffers();
        AL10.alBufferData(id, format, data, frequency);
    }

    public int getID(){
        return id;
    }

    public void delete(){
        AL10.alDeleteBuffers(id);
    }

    public static AudioBuffer fromFile(String path){

        WaveData data = WaveData.create(path);

        AudioBuffer buffer = new AudioBuffer(data.format, data.data, data.samplerate);

        data.dispose();

        return buffer;
    }

}
