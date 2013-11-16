/**
 * 
 */
package edu.uk.dromm.img;

import ij.process.ImageStatistics;

/**
 * @author dicardo
 *
 */
public interface EnhancementStrategy {

  EnhancementWorkflow workflow(ImageStatistics is);
}
