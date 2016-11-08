package com.github.sidit77.voxelworld.openal;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

public class AudioSource {

    /**
     * A simple wrapper around an OpenAL source object.
     */

    private int id;

    public AudioSource(){
        id = AL10.alGenSources();
    }

    public AudioSource setBuffer(AudioBuffer buffer){
        AL10.alSourceStop(id);
        AL10.alSourcei(id, AL10.AL_BUFFER, buffer.getID());
        return this;
    }

    public AudioSource play(){
        AL10.alSourcePlay(id);
        return this;
    }

    public AudioSource stop(){
        AL10.alSourceStop(id);
        return this;
    }

    public AudioSource pause(){
        AL10.alSourcePause(id);
        return this;
    }

    public AudioSource setVelocity(float x, float y, float z){
        AL10.alSource3f(id, AL10.AL_VELOCITY, x, y, z);
        return this;
    }

    public AudioSource setVeloctiy(Vector3f v){
        return setVelocity(v.x, v.y, v.z);
    }

    public AudioSource setLooping(boolean loop) {
        AL10.alSourcei(id, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
        return this;
    }

    public boolean isPlaying() {
        return AL10.alGetSourcei(id, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public AudioSource setVolume(float volume) {
        AL10.alSourcef(id, AL10.AL_GAIN, volume);
        return this;
    }

    public AudioSource setPitch(float pitch) {
        AL10.alSourcef(id, AL10.AL_PITCH, pitch);
        return this;
    }

    public AudioSource setPosition(float x, float y, float z) {
        AL10.alSource3f(id, AL10.AL_POSITION, x, y, z);
        return this;
    }

    public AudioSource setPosition(Vector3f v){
        return setPosition(v.x, v.y, v.z);
    }

    public int getID(){
        return id;
    }

    public void delete(){
        AL10.alDeleteSources(id);
    }

}
