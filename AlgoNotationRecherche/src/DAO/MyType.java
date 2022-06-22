package DAO;

import java.util.HashSet;
import java.util.Set;

public class MyType<N> {
	private Set<MyType<N>> children = new HashSet<>();
	private N Value;

	public MyType(N value) {
		super();
		Value = value;
	}

	public Set<MyType<N>> getChildren() {
		return children;
	}

	public void setChildren(Set<MyType<N>> children) {
		this.children = children;
	}

	public N getValue() {
		return Value;
	}

	public void setValue(N value) {
		Value = value;
	}

}