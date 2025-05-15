/* Licensed under Apache-2.0 2025. */
package github.benslabbert.vdw.codegen.example.jdbc;

import java.util.List;

record QueryParts(String sql, List<Object> params) {}
