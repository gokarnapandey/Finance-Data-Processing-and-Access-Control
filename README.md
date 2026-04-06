# Finance-Data-Processing-and-Access-Control
A high-performance, enterprise-grade REST API designed for tracking and analyzing personal or organizational financial health. Built with Spring Boot 3.x, this system integrates advanced security, automated auditing, and real-time data aggregation for a seamless financial management experience.


<h2>🏆 Engineering Highlights</h2>

<p>
This project is not a basic CRUD implementation. It is designed as a <b>secure, scalable, and production-oriented backend system</b> that demonstrates real-world backend engineering practices.
</p>

<hr/>

<h3>1. 🔐 Security & Identity Management</h3>

<ul>
  <li>
    <b>JWT-Based Authentication:</b> Implemented stateless authentication using JSON Web Tokens, eliminating server-side session storage and improving scalability.
  </li>
  <li>
    <b>Role-Based Access Control (RBAC):</b> Enforced strict authorization with clearly defined roles such as <b>ADMIN</b>, <b>ANALYST</b> and <b>VIEWER</b>.
  </li>
  <li>
    <b>Credential Security:</b> Passwords are securely hashed using <b>BCrypt</b>. Sensitive identifiers like email are immutable to prevent identity tampering.
  </li>
</ul>

<hr/>

<h3>2. ⚡ High-Performance Data Architecture</h3>

<ul>
  <li>
    <b>Server-Side Pagination:</b> Implemented using Spring <code>Pageable</code>, ensuring efficient data retrieval even with large datasets.
  </li>
  <li>
    <b>Dynamic Filtering (JPA Specifications):</b> Built a flexible query system allowing filtering by category, date range, and other parameters in a single optimized query.
  </li>
  <li>
    <b>ID Abstraction:</b> Internal database IDs (<code>Long</code>) are not exposed. Public APIs use external identifiers (e.g., <code>USR-501</code>, <code>REC-102</code>) to prevent enumeration attacks.
  </li>
</ul>

<hr/>

<h3>3. 📊 Business Intelligence & Analytics</h3>

<ul>
  <li>
    <b>Real-Time Dashboard:</b> Aggregated financial data into actionable insights such as total income, total expenses, and net balance.
  </li>
  <li>
    <b>Category-Based Analysis:</b> Enabled grouping of transactions for better financial visibility.
  </li>
  <li>
    <b>Soft Deletion Strategy:</b> Implemented logical deletion (<code>isDeleted</code>) to maintain audit trails and ensure data recoverability.
  </li>
</ul>

<hr/>

<h3>4. 🧾 Validation & API Documentation</h3>

<ul>
  <li>
    <b>DTO-Level Validation:</b> Applied Jakarta Validation (JSR-303) to enforce strict input constraints such as:
    <ul>
      <li>Regex-based password validation</li>
      <li>Mobile number format validation</li>
      <li>Non-null and non-blank constraints</li>
    </ul>
  </li>
  <li>
    <b>Swagger/OpenAPI Integration:</b> Fully documented APIs with interactive UI for testing and exploration.
  </li>
</ul>

<hr/>

<h3>5. 🏗️ Clean Architecture & Maintainability</h3>

<ul>
  <li>
    <b>DTO Pattern:</b> Strict separation between persistence layer (Entities) and API layer (DTOs) to avoid tight coupling.
  </li>
  <li>
    <b>Mapper Layer:</b> Centralized transformation logic ensuring controlled data mutation and security.
  </li>
  <li>
    <b>Audit Metadata:</b> Automated tracking of <code>createdAt</code>, <code>updatedAt</code>, and <code>createdBy</code> for full traceability.
  </li>
</ul>

<hr/>

<h3>⚠️ Design Trade-offs & Considerations</h3>

<ul>
  <li>JWT tokens are stateless → logout requires additional handling (e.g., token blacklist).</li>
  <li>Offset-based pagination may degrade at very high scale → cursor-based pagination can be introduced.</li>
  <li>Email is immutable → simplifies authentication but requires a separate flow for email updates.</li>
</ul>

<hr/>

<h3>📌 Key Takeaway</h3>

<p>
This system demonstrates a transition from <b>basic CRUD development</b> to <b>production-grade backend engineering</b>, focusing on security, scalability, and clean architecture principles.
</p>

<h2>🔐 Default Access & Testing Guide</h2>

<p>
To quickly test the application, use the default admin credentials provided below.
</p>

<hr/>

<h3>📌 System Information</h3>

<ul>
  <li><b>Database:</b> H2 In-Memory Database</li>
  <li><b>Behavior:</b> Data resets on every application restart</li>
</ul>

<hr/>

<h3>🔑 Default Admin Credentials</h3>

<table border="1" cellpadding="6">
  <tr>
    <th>Field</th>
    <th>Value</th>
  </tr>
  <tr>
    <td><b>Username (Email)</b></td>
    <td><code>system@admin.com</code></td>
  </tr>
  <tr>
    <td><b>Password</b></td>
    <td><code>Admin@12345</code></td>
  </tr>
</table>

<hr/>

<h3>🚀 How to Authenticate</h3>

<ol>
  <li>Call the <code>/login</code> endpoint</li>
  <li>Provide the credentials above</li>
  <li>Receive a <b>JWT Token</b></li>
  <li>Add it to request headers:</li>
</ol>

<pre>
Authorization: Bearer &lt;your_token_here&gt;
</pre>

<hr/>
<h2>📘 API Documentation (Swagger UI)</h2>

<p>
This project includes interactive API documentation using <b>Swagger (OpenAPI 3)</b>.
It allows you to explore and test all endpoints directly from the browser.
</p>

<hr/>

<h3>🔗 Access Swagger UI</h3>

<p>
Once the application is running, open:
</p>

<pre>
http://localhost:8080/swagger-ui/index.html
</pre>

<h3>🚀 How to Use Swagger</h3>

<ol>
  <li>Open the Swagger UI link</li>
  <li>Locate the <b>/login</b> endpoint</li>
  <li>Enter default admin credentials:</li>
</ol>

<table border="1" cellpadding="6">
  <tr>
    <th>Field</th>
    <th>Value</th>
  </tr>
  <tr>
    <td>Username</td>
    <td><code>system@admin.com</code></td>
  </tr>
  <tr>
    <td>Password</td>
    <td><code>Admin@12345</code></td>
  </tr>
</table>

<ol start="4">
  <li>Execute the request to receive a <b>JWT Token</b></li>
  <li>Click the <b>"Authorize"</b> button (top right)</li>
  <li>Enter:</li>
</ol>

<pre>
Bearer &lt;your_token_here&gt;
</pre>

<ol start="7">
  <li>Now you can access all secured APIs</li>
</ol>



<hr/>

<h2>🧪 Unit Testing & Code Reliability</h2>

<p>
The project includes comprehensive <b>unit tests for the Service Layer</b> to ensure correctness of business logic,
security rules, and data integrity without relying on external systems like the database.
</p>

<hr/>

<h3>📌 Testing Strategy</h3>

<ul>
  <li><b>Layer Tested:</b> Service Implementation Layer</li>
  <li><b>Testing Framework:</b> JUnit 5</li>
  <li><b>Mocking Framework:</b> Mockito</li>
  <li><b>Approach:</b> Isolated unit testing (no Spring context, no DB dependency)</li>
</ul>

<hr/>

<h3>✔ Test Coverage Focus</h3>

<ul>
  <li><b>User Management:</b>
    <ul>
      <li>User creation validation</li>
      <li>Duplicate email/mobile handling</li>
      <li>Role-based restrictions</li>
      <li>Status update logic</li>
    </ul>
  </li>

  <li><b>Financial Records:</b>
    <ul>
      <li>Record creation and updates</li>
      <li>Filtering and pagination logic</li>
      <li>Category-based operations</li>
    </ul>
  </li>

  <li><b>Security Logic:</b>
    <ul>
      <li>Authorization checks (ADMIN vs others)</li>
      <li>Immutable field protection (email)</li>
    </ul>
  </li>
</ul>

<hr/>

<h3>⚙️ Key Testing Practices</h3>

<ul>
  <li>Repository layer is <b>mocked</b> to isolate business logic</li>
  <li>Edge cases and exception scenarios are explicitly tested</li>
  <li>No dependency on database → <b>fast and deterministic tests</b></li>
</ul>

<hr/>

<h3>▶️ Running Tests</h3>

<pre>
mvn test
</pre>

<p>
All unit tests execute independently and validate service-level behavior without requiring application startup.
</p>

<hr/>

<h3>⚠️ Design Considerations</h3>

<ul>
  <li>Unit tests focus on logic, not integration</li>
  <li>Security annotations (@PreAuthorize) are not directly tested → should be covered in integration tests</li>
  <li>Future improvement: Add <b>integration tests</b> using @SpringBootTest</li>
</ul>

<hr/>

<h2>🚦 Rate Limiting (Traffic Control Layer)</h2>

<p>
This system includes a <b>custom in-memory Token Bucket Rate Limiter</b> integrated into the 
<b>Spring Security filter chain</b>. It acts as a <b>pre-controller gatekeeper</b> to ensure controlled 
and fair API usage.
</p>

<h3>🧠 Architecture Components</h3>

<ul>
  <li>
    <b>Token Bucket Engine (Bucket):</b>
    <ul>
      <li>Manages request allowance using tokens</li>
      <li>Supports burst traffic with controlled refill</li>
      <li>Uses nanosecond precision for accurate timing</li>
      <li>Thread-safe using synchronized methods</li>
    </ul>
  </li>

  <li>
    <b>Key Resolver:</b>
    <ul>
      <li>Identifies user using <code>username/IP + endpoint</code></li>
      <li>Enables per-user and per-endpoint rate limiting</li>
    </ul>
  </li>

  <li>
    <b>Rate Limiter Service:</b>
    <ul>
      <li>Uses <code>ConcurrentHashMap&lt;key, Bucket&gt;</code></li>
      <li>Creates and manages buckets dynamically</li>
    </ul>
  </li>

  <li>
    <b>Rate Limit Filter:</b>
    <ul>
      <li>Intercepts requests after authentication</li>
      <li>Blocks excessive traffic before reaching controller</li>
    </ul>
  </li>
</ul>


<h3>⚙️ Rate Limiting Strategy</h3>

<p><b>Configuration:</b></p>

<pre>
Capacity (MAX_TOKENS): 5
Refill: 5 tokens every 10 seconds
</pre>

<h3>🔄 How It Works</h3>

<ol>
  <li>Request arrives at server</li>
  <li>User is identified (JWT username or IP fallback)</li>
  <li>Bucket is retrieved or created</li>
  <li>Refill logic calculates elapsed time</li>
  <li>Token is consumed</li>
  <li>If token exists → request allowed</li>
  <li>If no token → request rejected</li>
</ol>

<h3>📊 Behavior Summary</h3>

<table border="1" cellpadding="6">
  <tr>
    <th>Scenario</th>
    <th>System Behavior</th>
  </tr>
  <tr>
    <td>First 5 Requests</td>
    <td>HTTP 200 OK</td>
  </tr>
  <tr>
    <td>6th Request (within 10s)</td>
    <td>HTTP 429 Too Many Requests</td>
  </tr>
  <tr>
    <td>After 10 seconds</td>
    <td>Request allowed (tokens refilled)</td>
  </tr>
</table>

<h3>📉 Important Characteristics</h3>

<ul>
  <li>Supports burst traffic (up to 5 immediate requests)</li>
  <li>Refill happens every 10 seconds</li>
  <li>Thread-safe implementation</li>
</ul>


<h3>⚠️ Limitations</h3>

<ul>
  <li>Not a strict sliding window (burst allowed after refill)</li>
  <li>In-memory storage (not suitable for distributed systems)</li>
  <li>No automatic eviction (potential memory growth)</li>
</ul>

<h3>🔐 Security Benefits</h3>

<ul>
  <li>Prevents brute force attacks</li>
  <li>Reduces risk of Denial of Service (DoS)</li>
  <li>Ensures fair resource usage</li>
  <li>Protects backend from excessive traffic</li>
</ul>

<h3>🧪 Example Scenarios</h3>

<p><b>Normal Usage:</b></p>
<pre>
User sends 5 requests → All succeed
</pre>

<p><b>Rate Limit Exceeded:</b></p>
<pre>
6th request within 10 seconds → HTTP 429
</pre>

<p><b>After Cooldown:</b></p>
<pre>
Wait 10 seconds → Requests allowed again
</pre>

<h3>⚠️ Design Trade-offs</h3>

<table border="1" cellpadding="6">
  <tr>
    <th>Aspect</th>
    <th>Decision</th>
    <th>Impact</th>
  </tr>
  <tr>
    <td>Storage</td>
    <td>In-memory</td>
    <td>Fast but not scalable</td>
  </tr>
  <tr>
    <td>Algorithm</td>
    <td>Token Bucket</td>
    <td>Allows burst traffic</td>
  </tr>
  <tr>
    <td>Placement</td>
    <td>Security Filter</td>
    <td>Efficient early rejection</td>
  </tr>
</table>

<h3>🚀 Future Improvements</h3>

<ul>
  <li>Use Redis for distributed rate limiting</li>
  <li>Implement Sliding Window for strict limits</li>
  <li>Add cache eviction (Caffeine)</li>
  <li>Move to API Gateway level (NGINX / Spring Cloud Gateway)</li>
</ul>

<h3>📌 Key Takeaway</h3>

<p>
This implementation demonstrates a shift from <b>basic request handling</b> to 
<b>production-grade traffic control</b>, improving system stability, security, and scalability.
</p>

<h3>⚠️ Important Notes</h3>

<ul>
  <li>All protected APIs require a valid JWT token</li>
  <li>Token is stateless (no server-side session)</li>
  <li>H2 database is non-persistent (data will be lost after restart)</li>
</ul>
