<h1>Finance-Data-Processing-and-Access-Control</h1>

<p>
A high-performance, enterprise-grade REST API for tracking and analyzing financial data.
Built with Spring Boot 3.x focusing on scalability, security, and clean architecture.
</p>

<section>
  <h2>🏆 Engineering Highlights</h2>
  <p>
    This is not a basic CRUD system. It demonstrates production-grade backend practices.
  </p>
</section>

<section>
  <h2>🔐 Security & Identity Management</h2>
  <ul>
    <li><b>JWT Authentication:</b> Stateless authentication without server sessions</li>
    <li><b>RBAC:</b> Roles - ADMIN, ANALYST, VIEWER</li>
    <li><b>Credential Security:</b> BCrypt hashing and immutable email</li>
  </ul>
</section>

<section>
  <h2>⚡ High-Performance Data Architecture</h2>
  <ul>
    <li><b>Pagination:</b> Spring Pageable for large datasets</li>
    <li><b>Dynamic Filtering:</b> JPA Specifications</li>
    <li><b>ID Abstraction:</b> External IDs like USR-501</li>
  </ul>
</section>

<section>
  <h2>📊 Business Intelligence</h2>
  <ul>
    <li>Real-time dashboard insights</li>
    <li>Category-based aggregation</li>
    <li>Soft delete using <code>isDeleted</code></li>
  </ul>
</section>

<section>
  <h2>🧾 Validation & Documentation</h2>
  <ul>
    <li>JSR-303 validation (regex, constraints)</li>
    <li>Swagger (OpenAPI 3) integration</li>
  </ul>
</section>

<section>
  <h2>🏗️ Clean Architecture</h2>
  <ul>
    <li>DTO pattern (no entity exposure)</li>
    <li>Mapper layer for transformations</li>
    <li>Audit fields: createdAt, updatedAt, createdBy</li>
  </ul>
</section>

<section>
  <h2>⚠️ Design Trade-offs</h2>
  <ul>
    <li>JWT logout requires token blacklist</li>
    <li>Offset pagination not scalable at high scale</li>
    <li>Email immutability complicates updates</li>
  </ul>
</section>

<section>
  <h2>🔐 Default Access</h2>

  <h3>System Info</h3>
  <ul>
    <li><b>Database:</b> H2 (in-memory)</li>
    <li><b>Behavior:</b> resets on restart</li>
  </ul>

  <h3>Admin Credentials</h3>
  <table>
    <tr><th>Field</th><th>Value</th></tr>
    <tr><td>Email</td><td><code>system@admin.com</code></td></tr>
    <tr><td>Password</td><td><code>Admin@12345</code></td></tr>
  </table>
</section>

<section>
  <h2>🚀 Authentication Flow</h2>
  <ol>
    <li>Call <code>/login</code></li>
    <li>Provide credentials</li>
    <li>Receive JWT token</li>
    <li>Add header:</li>
  </ol>

  <pre><code>Authorization: Bearer &lt;token&gt;</code></pre>
</section>

<section>
  <h2>📘 Swagger API Docs</h2>

  <p>Access:</p>
  <pre><code>http://localhost:8080/swagger-ui/index.html</code></pre>

  <h3>Steps</h3>
  <ol>
    <li>Open Swagger UI</li>
    <li>Call <code>/login</code></li>
    <li>Copy JWT</li>
    <li>Click Authorize</li>
    <li>Add:</li>
  </ol>

  <pre><code>Bearer &lt;token&gt;</code></pre>
</section>

<section>
  <h2>🧪 Unit Testing</h2>

  <h3>Strategy</h3>
  <ul>
    <li>Service layer testing</li>
    <li>JUnit 5 + Mockito</li>
    <li>No DB dependency</li>
  </ul>

  <h3>Run</h3>
  <pre><code>mvn test</code></pre>
</section>

<section>
  <h2>🚦 Rate Limiting (Token Bucket)</h2>

  <h3>Configuration</h3>
  <pre><code>
Capacity: 5
Refill: 5 tokens / 10 sec
  </code></pre>

  <h3>Flow</h3>
  <ol>
    <li>Request arrives</li>
    <li>Identify user</li>
    <li>Fetch bucket</li>
    <li>Consume token</li>
    <li>Allow / Reject</li>
  </ol>

  <h3>Behavior</h3>
  <table>
    <tr><th>Scenario</th><th>Result</th></tr>
    <tr><td>First 5 requests</td><td>200 OK</td></tr>
    <tr><td>6th request</td><td>429</td></tr>
    <tr><td>After 10 sec</td><td>Allowed</td></tr>
  </table>

  <h3>Limitations</h3>
  <ul>
    <li>In-memory (not distributed)</li>
    <li>No eviction (memory risk)</li>
    <li>Not strict rate limiting</li>
  </ul>
</section>

<section>
  <h2>📌 Final Notes</h2>
  <ul>
    <li>JWT required for all APIs</li>
    <li>Stateless authentication</li>
    <li>H2 data resets on restart</li>
  </ul>
</section>
