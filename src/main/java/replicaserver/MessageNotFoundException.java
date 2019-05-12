package replicaserver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	private int[] msgNum = null;
}
