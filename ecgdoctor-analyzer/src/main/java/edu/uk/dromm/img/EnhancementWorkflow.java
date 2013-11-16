package edu.uk.dromm.img;

import ij.process.ByteProcessor;

/**
 * 
 * @author dicardo
 *
 */
public interface EnhancementWorkflow {

  void execute(ByteProcessor ip);
}
