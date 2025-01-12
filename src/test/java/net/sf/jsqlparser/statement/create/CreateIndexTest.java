/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement.create;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static net.sf.jsqlparser.test.TestUtils.assertSqlCanBeParsedAndDeparsed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CreateIndexTest {

    private final CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testCreateIndex() throws JSQLParserException {
        String statement = "CREATE INDEX myindex ON mytab (mycol, mycol2)";
        CreateIndex createIndex = (CreateIndex) parserManager.parse(new StringReader(statement));
        assertEquals(2, createIndex.getIndex().getColumnsNames().size());
        assertEquals("myindex", createIndex.getIndex().getName());
        assertNull(createIndex.getIndex().getType());
        assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        assertEquals("mycol", createIndex.getIndex().getColumnsNames().get(0));
        assertEquals(statement, "" + createIndex);
    }

    @Test
    public void testCreateIndex2() throws JSQLParserException {
        String statement = "CREATE mytype INDEX myindex ON mytab (mycol, mycol2)";
        CreateIndex createIndex = (CreateIndex) parserManager.parse(new StringReader(statement));
        assertEquals(2, createIndex.getIndex().getColumnsNames().size());
        assertEquals("myindex", createIndex.getIndex().getName());
        assertEquals("mytype", createIndex.getIndex().getType());
        assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        assertEquals("mycol2", createIndex.getIndex().getColumnsNames().get(1));
        assertEquals(statement, "" + createIndex);
    }

    @Test
    public void testCreateIndex3() throws JSQLParserException {
        String statement = "CREATE mytype INDEX myindex ON mytab (mycol ASC, mycol2, mycol3)";
        CreateIndex createIndex = (CreateIndex) parserManager.parse(new StringReader(statement));
        assertEquals(3, createIndex.getIndex().getColumnsNames().size());
        assertEquals("myindex", createIndex.getIndex().getName());
        assertEquals("mytype", createIndex.getIndex().getType());
        assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        assertEquals("mycol3", createIndex.getIndex().getColumnsNames().get(2));
    }

    @Test
    public void testCreateIndex4() throws JSQLParserException {
        String statement = "CREATE mytype INDEX myindex ON mytab (mycol ASC, mycol2 (75), mycol3)";
        CreateIndex createIndex = (CreateIndex) parserManager.parse(new StringReader(statement));
        assertEquals(3, createIndex.getIndex().getColumnsNames().size());
        assertEquals("myindex", createIndex.getIndex().getName());
        assertEquals("mytype", createIndex.getIndex().getType());
        assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        assertEquals("mycol3", createIndex.getIndex().getColumnsNames().get(2));
    }

    @Test
    public void testCreateIndex5() throws JSQLParserException {
        String statement =
                "CREATE mytype INDEX myindex ON mytab (mycol ASC, mycol2 (75), mycol3) mymodifiers";
        CreateIndex createIndex = (CreateIndex) parserManager.parse(new StringReader(statement));
        assertEquals(3, createIndex.getIndex().getColumnsNames().size());
        assertEquals("myindex", createIndex.getIndex().getName());
        assertEquals("mytype", createIndex.getIndex().getType());
        assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        assertEquals("mycol3", createIndex.getIndex().getColumnsNames().get(2));
    }

    @Test
    public void testCreateIndex6() throws JSQLParserException {
        String stmt = "CREATE INDEX myindex ON mytab (mycol, mycol2)";
        assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCreateIndex7() throws JSQLParserException {
        String statement = "CREATE INDEX myindex1 ON mytab USING GIST (mycol)";
        CreateIndex createIndex = (CreateIndex) parserManager.parse(new StringReader(statement));
        assertEquals(1, createIndex.getIndex().getColumnsNames().size());
        assertEquals("myindex1", createIndex.getIndex().getName());
        assertNull(createIndex.getIndex().getType());
        assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        assertEquals("mycol", createIndex.getIndex().getColumnsNames().get(0));
        assertEquals("GIST", createIndex.getIndex().getUsing());
        assertEquals(statement, "" + createIndex);
        assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testCreateIndexIssue633() throws JSQLParserException {
        assertSqlCanBeParsedAndDeparsed(
                "CREATE INDEX idx_american_football_action_plays_1 ON american_football_action_plays USING btree (play_type)");
    }

    @Test
    public void testFullIndexNameIssue936() throws JSQLParserException {
        assertSqlCanBeParsedAndDeparsed(
                "CREATE INDEX \"TS\".\"IDX\" ON \"TEST\" (\"ID\" ASC) TABLESPACE \"TS\"");
    }

    @Test
    public void testFullIndexNameIssue936_2() throws JSQLParserException {
        assertSqlCanBeParsedAndDeparsed(
                "CREATE INDEX \"TS\".\"IDX\" ON \"TEST\" (\"ID\") TABLESPACE \"TS\"");
    }

    @Test
    public void testCreateIndexTrailingOptions() throws JSQLParserException {
        String statement = "CREATE UNIQUE INDEX cfe.version_info_idx2\n"
                + "    ON cfe.version_info ( major_version\n"
                + "                            , minor_version\n"
                + "                            , patch_level ) parallel compress nologging\n"
                + ";";
        CreateIndex createIndex = (CreateIndex) parserManager.parse(new StringReader(statement));
        List<String> tailParameters = createIndex.getTailParameters();
        assertEquals(3, tailParameters.size());
        assertEquals(tailParameters.get(0), "parallel");
        assertEquals(tailParameters.get(1), "compress");
        assertEquals(tailParameters.get(2), "nologging");
    }

    @Test
    void testIfNotExistsIssue1861() throws JSQLParserException {
        String sqlStr =
                "CREATE INDEX IF NOT EXISTS test_test_idx ON test.test USING btree (\"time\")";
        assertSqlCanBeParsedAndDeparsed(sqlStr, true);
    }
}
