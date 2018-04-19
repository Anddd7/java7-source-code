package github.eddy.java7core.io.model;

import java.io.Serializable;

/**
 * @author and777
 * @date 2018/1/12
 */

public class SerializableLine implements Serializable {

  private static final long serialVersionUID = 4370121844819715392L;
  public long id;
  public SerializablePoint point;
  public Boolean valid;
}



