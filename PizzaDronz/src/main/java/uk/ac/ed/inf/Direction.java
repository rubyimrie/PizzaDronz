package uk.ac.ed.inf;

/**
 * An enum that holds constants for all sixteen compass direction
 */
public enum Direction {
    //constants for the 16 compass angles
     E, ENE, NE, NNE, N, NNW, NW,
    WNW, W, WSW, SW, SSW, S, SSE,
    SE, ESE ;

    private Direction opposite;

    static {
        E.opposite=W;
        ENE.opposite=WSW;
        NE.opposite=SW;
        NNE.opposite=SSW;
        N.opposite=S;
        NNW.opposite=SSE;
        NW.opposite=SE;
        WNW.opposite=ESE;
        W.opposite=E;
        WSW.opposite=ENE;
        SW.opposite=NE;
        SSW.opposite=NNE;
        S.opposite=N;
        SSE.opposite=NNW;
        SE.opposite=NW;
        ESE.opposite=WNW;

    }
    public Direction getOppositeDirection() {
        return opposite;
    }

}
