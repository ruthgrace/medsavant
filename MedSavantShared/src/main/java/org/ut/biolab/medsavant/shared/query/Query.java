package org.ut.biolab.medsavant.shared.query;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for the queries, implementation agnostic.
 */
public interface Query {


    /**
     * Execute the current query and return a list of the type T objects.
     * @param <T>               The type of entity returned by the query.
     * @return                  A list of type T objects
     * @throws QueryException
     */
    <T> List<T> execute() throws QueryException;

    /**
     * Execute the current query and return a list of the type ResultRow, each containing
     * a subset of the entity fields.
     * @return                  A list of ResultRow objects.
     */
    public List<ResultRow> executeForRows();

    /**
     * Return the string query statement.
     * @return                  The query statement as a String.
     */
    public String getStatement();

    /**
     * Set the query statement string.
     * @param statement         The query statement as a String
     */
    public void setStatement(String statement);

    /**
     * Bind a paramter value to the named parameter paramName in the query.
     * @param parameterName             The name of the parameter, appears as :parameterName in the query.
     * @param value                     The object corresponding to the parameter.
     * @return                          The query.
     * @throws IllegalArgumentException If the parameter name does not corresponding to a parameter in the query.
     */
    public Query setParameter(String parameterName, Object value);

    /**
     * Bind a paramter value to the index-th parameter in the query.
     * @param index                     The index of the parameter, appears as ?index in the query.
     * @param value                     he object corresponding to the parameter.
     * @return                          The query.
     * @throws IllegalArgumentException If the parameter does not correspond to a parameter in the query.
     */
    public Query setParameter(int index, Object value);

    /**
     * Get the parameter objects associated with this query.
     *
     * @return      The set of parameter objects associated with this query.
     */
    public Collection<Object> getParameters();

    /**
     * Get the names of the parameters associated with this query.
     * @return      The set of parameters associated with this query.
     */
    public Set<String> getParameterNames();

    /**
     *
     * @return
     */
    public Map<String, Object> getParameterMap();

    /**
     * Specifies where in the result set should the returned results start.
     * @param start     start of the results
     * @return          The query
     */
    Query setStart(int start);

    /**
     * @return          the start of the result list.
     */
    public int getStart();

    /**
     * Specifies the maximum number of results returned by this query. Similar to the SQL limit.
     * @param limit      The maximum number of results to be returned
     * @return           The query
     */
    Query setLimit(int limit);

    /**
     * @return          the limit of the result list.
     */
    public int getLimit();


}