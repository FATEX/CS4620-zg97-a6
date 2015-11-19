package cs4620.anim;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cs4620.common.Scene;
import cs4620.common.SceneObject;
import cs4620.common.event.SceneTransformationEvent;
import egl.math.Matrix4;
import egl.math.Vector3;
import egl.math.Matrix3;
import egl.math.Quat;

/**
 * A Component Resting Upon Scene That Gives
 * Animation Capabilities
 * @author Cristian
 *
 */
public class AnimationEngine {
	/**
	 * The First Frame In The Global Timeline
	 */
	private int frameStart = 0;
	/**
	 * The Last Frame In The Global Timeline
	 */
	private int frameEnd = 100;
	/**
	 * The Current Frame In The Global Timeline
	 */
	private int curFrame = 0;
	/**
	 * Scene Reference
	 */
	private final Scene scene;
	/**
	 * Animation Timelines That Map To Object Names
	 */
	public final HashMap<String, AnimTimeline> timelines = new HashMap<>();

	/**
	 * An Animation Engine That Works Only On A Certain Scene
	 * @param s The Working Scene
	 */
	public AnimationEngine(Scene s) {
		scene = s;
	}
	
	/**
	 * Set The First And Last Frame Of The Global Timeline
	 * @param start First Frame
	 * @param end Last Frame (Must Be Greater Than The First
	 */
	public void setTimelineBounds(int start, int end) {
		// Make Sure Our End Is Greater Than Our Start
		if(end < start) {
			int buf = end;
			end = start;
			start = buf;
		}
		
		frameStart = start;
		frameEnd = end;
		moveToFrame(curFrame);
	}
	/**
	 * Add An Animating Object
	 * @param oName Object Name
	 * @param o Object
	 */
	public void addObject(String oName, SceneObject o) {
		timelines.put(oName, new AnimTimeline(o));
	}
	/**
	 * Remove An Animating Object
	 * @param oName Object Name
	 */
	public void removeObject(String oName) {
		timelines.remove(oName);
	}

	/**
	 * Set The Frame Pointer To A Desired Frame (Will Be Bounded By The Global Timeline)
	 * @param f Desired Frame
	 */
	public void moveToFrame(int f) {
		if(f < frameStart) f = frameStart;
		else if(f > frameEnd) f = frameEnd;
		curFrame = f;
	}
	/**
	 * Looping Forwards Play
	 * @param n Number Of Frames To Move Forwards
	 */
	public void advance(int n) {
		curFrame += n;
		if(curFrame > frameEnd) curFrame = frameStart + (curFrame - frameEnd - 1);
	}
	/**
	 * Looping Backwards Play
	 * @param n Number Of Frames To Move Backwards
	 */
	public void rewind(int n) {
		curFrame -= n;
		if(curFrame < frameStart) curFrame = frameEnd - (frameStart - curFrame - 1);
	}

	public int getCurrentFrame() {
		return curFrame;
	}
	public int getFirstFrame() {
		return frameStart;
	}
	public int getLastFrame() {
		return frameEnd;
	}
	public int getNumFrames() {
		return frameEnd - frameStart + 1;
	}

	/**
	 * Adds A Keyframe For An Object At The Current Frame
	 * Using The Object's Transformation - (CONVENIENCE METHOD)
	 * @param oName Object Name
	 */
	public void addKeyframe(String oName) {
		AnimTimeline tl = timelines.get(oName);
		if(tl == null) return;
		tl.addKeyFrame(getCurrentFrame(), tl.object.transformation);
	}
	/**
	 * Removes A Keyframe For An Object At The Current Frame
	 * Using The Object's Transformation - (CONVENIENCE METHOD)
	 * @param oName Object Name
	 */
	public void removeKeyframe(String oName) {
		AnimTimeline tl = timelines.get(oName);
		if(tl == null) return;
		tl.removeKeyFrame(getCurrentFrame(), tl.object.transformation);
	}
	
	/**
	 * Loops Through All The Animating Objects And Updates Their Transformations To
	 * The Current Frame - For Each Updated Transformation, An Event Has To Be 
	 * Sent Through The Scene Notifying Everyone Of The Change
	 */

	// TODO A6 - Animation

	 public void updateTransformations() {
		 // Loop Through All The Timelines
		 // And Update Transformations Accordingly
		 // (You WILL Need To Use this.scene)
		 Iterator it = this.timelines.entrySet().iterator();
		 while (it.hasNext()) {
			 Map.Entry pair = (Map.Entry)it.next();
			 AnimTimeline timeline = (AnimTimeline) (pair.getValue());
			 AnimKeyframe[] outPair = new AnimKeyframe[2];
			 timeline.getSurroundingFrames(curFrame, outPair);
			 float ratio = getRatio(outPair[0].frame, outPair[1].frame, curFrame);
			 
			 Vector3 prevT = outPair[0].transformation.getTrans();
			 Vector3 nextT = outPair[1].transformation.getTrans();
			 
			 Matrix3 prevR = new Matrix3();
			 Matrix3 prevS = new Matrix3();
			 outPair[0].transformation.getAxes().polar_decomp(prevR, prevS);
			 
			 Matrix3 nextR = new Matrix3();
			 Matrix3 nextS = new Matrix3();
			 outPair[1].transformation.getAxes().polar_decomp(nextR, nextS);
			 //System.out.println(pair.getKey() + " = " + pair.getValue());
			 it.remove(); // avoids a ConcurrentModificationException
			 
		 }
		 
			 
		// get pair of surrounding frames
		// (function in AnimTimeline)

		// get interpolation ratio

		// interpolate translations linearly

		// polar decompose axis matrices

		// slerp rotation matrix and linearly interpolate scales

		// combine interpolated R,S,and T


	 }

         public static float getRatio(int min, int max, int cur) {
	     if(min == max) return 0f;
	     float total = max - min;
	     float diff = cur - min;
	     return diff / total;
	 }
}
