package greact.solver;

import rx.Observable;

/**
 * Created by thiago on 03/12/16.
 */
public interface Solver<R> {

    Observable<R> solve();
}
