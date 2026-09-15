package com.example.data.template

data class TemplateDefinition(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val initialFiles: Map<String, String>,
    val defaultActiveFile: String = "src/App.jsx"
)

object DefaultTemplates {

    val REACT_SAAS = TemplateDefinition(
        id = "react_saas",
        name = "SaaS Landing & Web App",
        description = "Modern React + Tailwind SaaS landing page with interactive auth modal & pricing switcher",
        iconName = "rocket",
        defaultActiveFile = "src/App.jsx",
        initialFiles = mapOf(
            "package.json" to """{
  "name": "bolt-saas-platform",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build"
  },
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "lucide-react": "^0.344.0"
  },
  "devDependencies": {
    "vite": "^5.4.2",
    "tailwindcss": "^3.4.1"
  }
}""",
            "index.html" to """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AeroFlow - Cloud AI Engine</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/react@18/umd/react.production.min.js"></script>
  <script src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"></script>
  <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap');
    body { font-family: 'Inter', sans-serif; }
  </style>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen">
  <div id="root"></div>
  <script type="text/babel" src="src/App.jsx"></script>
</body>
</html>""",
            "src/App.jsx" to """function App() {
  const [billingCycle, setBillingCycle] = React.useState('monthly');
  const [authModal, setAuthModal] = React.useState(false);
  const [metricsCount, setMetricsCount] = React.useState(14820);
  const [authMode, setAuthMode] = React.useState('signup');

  const plans = [
    {
      name: 'Starter',
      price: billingCycle === 'monthly' ? '$29' : '$24',
      features: ['5 Active Microservices', '100k API Requests/mo', 'Realtime Monitoring', 'Community Support']
    },
    {
      name: 'Professional',
      price: billingCycle === 'monthly' ? '$79' : '$64',
      popular: true,
      features: ['Unlimited Microservices', '2M API Requests/mo', 'Bolt AI Autopilot', 'Priority 24/7 SLA', 'Custom Domains']
    },
    {
      name: 'Enterprise',
      price: 'Custom',
      features: ['Dedicated VPC', 'Unlimited Bandwidth', 'SOC2 Compliance', 'Dedicated Solutions Architect']
    }
  ];

  return (
    <div class="min-h-screen bg-slate-950 text-slate-100 pb-16">
      {/* Navbar */}
      <nav class="border-b border-slate-800 bg-slate-900/80 backdrop-blur sticky top-0 z-40 px-6 py-4 flex items-center justify-between">
        <div class="flex items-center space-x-3">
          <div class="w-9 h-9 rounded-xl bg-gradient-to-tr from-violet-600 to-cyan-400 flex items-center justify-center font-black text-white shadow-lg shadow-violet-500/30">
            ⚡
          </div>
          <span class="text-xl font-bold tracking-tight bg-gradient-to-r from-violet-400 via-cyan-300 to-indigo-300 bg-clip-text text-transparent">
            AeroFlow.io
          </span>
        </div>
        <div class="flex items-center space-x-4">
          <button 
            onClick={() => { setAuthMode('login'); setAuthModal(true); }}
            class="text-sm text-slate-300 hover:text-white transition px-3 py-1.5"
          >
            Sign In
          </button>
          <button 
            onClick={() => { setAuthMode('signup'); setAuthModal(true); }}
            class="text-sm bg-violet-600 hover:bg-violet-500 text-white font-medium px-4 py-2 rounded-lg transition shadow-md shadow-violet-600/30 active:scale-95"
          >
            Get Started Free
          </button>
        </div>
      </nav>

      {/* Hero Section */}
      <section class="max-w-5xl mx-auto px-6 pt-16 pb-12 text-center">
        <div class="inline-flex items-center space-x-2 bg-violet-500/10 border border-violet-500/30 px-3.5 py-1 rounded-full text-xs font-semibold text-violet-300 mb-6">
          <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
          <span>Bolt.diy Autonomous Agent Core v3.1</span>
        </div>
        <h1 class="text-4xl sm:text-6xl font-extrabold tracking-tight text-white max-w-3xl mx-auto leading-tight">
          Supercharge your <span class="bg-gradient-to-r from-violet-400 via-fuchsia-400 to-cyan-400 bg-clip-text text-transparent">Cloud Workflows</span> with Intelligent Autopilot
        </h1>
        <p class="text-slate-400 text-base sm:text-lg max-w-2xl mx-auto mt-6">
          Develop, scaffold, and deploy full-stack cloud containers directly from your pocket. Designed with deep reasoning and sub-second compilation.
        </p>

        {/* Live Interactive Counter */}
        <div class="mt-8 flex flex-wrap justify-center gap-4">
          <button 
            onClick={() => { setMetricsCount(c => c + 1); console.log("Deployment dispatched!"); }}
            class="bg-gradient-to-r from-violet-600 to-cyan-500 hover:opacity-95 text-white font-semibold px-6 py-3 rounded-xl shadow-lg shadow-violet-600/25 transition active:scale-95 flex items-center space-x-2"
          >
            <span>🚀 Run Live Deployment</span>
            <span class="bg-black/30 px-2 py-0.5 rounded text-xs">+{metricsCount}</span>
          </button>
          <button 
            onClick={() => setAuthModal(true)}
            class="border border-slate-700 hover:bg-slate-800 text-slate-200 px-6 py-3 rounded-xl font-medium transition"
          >
            Explore API Sandbox
          </button>
        </div>
      </section>

      {/* Pricing Grid */}
      <section class="max-w-5xl mx-auto px-6 py-12">
        <div class="text-center mb-10">
          <h2 class="text-2xl sm:text-3xl font-bold">Predictable, transparent pricing</h2>
          <div class="mt-4 inline-flex items-center bg-slate-900 border border-slate-800 p-1 rounded-xl">
            <button 
              onClick={() => setBillingCycle('monthly')}
              class={"px-4 py-1.5 rounded-lg text-xs font-semibold transition " + (billingCycle === 'monthly' ? "bg-violet-600 text-white" : "text-slate-400")}
            >
              Monthly
            </button>
            <button 
              onClick={() => setBillingCycle('annual')}
              class={"px-4 py-1.5 rounded-lg text-xs font-semibold transition flex items-center space-x-1 " + (billingCycle === 'annual' ? "bg-violet-600 text-white" : "text-slate-400")}
            >
              <span>Yearly</span>
              <span class="text-[10px] bg-emerald-500/20 text-emerald-300 px-1.5 rounded">Save 20%</span>
            </button>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          {plans.map((p, idx) => (
            <div key={idx} class={"rounded-2xl p-6 border transition flex flex-col justify-between " + (p.popular ? "border-violet-500 bg-gradient-to-b from-violet-950/40 to-slate-900 shadow-xl shadow-violet-900/20" : "border-slate-800 bg-slate-900/70")}>
              <div>
                {p.popular && <span class="bg-violet-600 text-white text-[11px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full mb-3 inline-block">Recommended</span>}
                <h3 class="text-lg font-bold">{p.name}</h3>
                <div class="my-4">
                  <span class="text-3xl font-extrabold">{p.price}</span>
                  {p.price !== 'Custom' && <span class="text-slate-400 text-sm"> / month</span>}
                </div>
                <ul class="space-y-2.5 text-sm text-slate-300">
                  {p.features.map((f, fidx) => (
                    <li key={fidx} class="flex items-center space-x-2">
                      <span class="text-emerald-400">✓</span>
                      <span>{f}</span>
                    </li>
                  ))}
                </ul>
              </div>
              <button 
                onClick={() => setAuthModal(true)}
                class={"mt-6 w-full py-2.5 rounded-xl font-medium text-sm transition " + (p.popular ? "bg-violet-600 hover:bg-violet-500 text-white" : "bg-slate-800 hover:bg-slate-700 text-slate-200")}
              >
                Choose Plan
              </button>
            </div>
          ))}
        </div>
      </section>

      {/* Auth Modal */}
      {authModal && (
        <div class="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div class="bg-slate-900 border border-slate-700 rounded-2xl max-w-sm w-full p-6 shadow-2xl relative">
            <button 
              onClick={() => setAuthModal(false)}
              class="absolute top-4 right-4 text-slate-400 hover:text-white"
            >
              ✕
            </button>
            <h3 class="text-xl font-bold mb-1">{authMode === 'signup' ? 'Create your Account' : 'Welcome Back'}</h3>
            <p class="text-xs text-slate-400 mb-4">Start your 14-day free trial on Bolt Cloud.</p>
            <form onSubmit={(e) => { e.preventDefault(); alert("Auth simulation success! Connected to virtual backend."); setAuthModal(false); }}>
              <input type="email" placeholder="work@company.com" required class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white mb-3 focus:outline-none focus:border-violet-500" />
              <input type="password" placeholder="Password" required class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white mb-4 focus:outline-none focus:border-violet-500" />
              <button type="submit" class="w-full bg-violet-600 hover:bg-violet-500 text-white py-2.5 rounded-lg font-medium text-sm transition shadow-lg shadow-violet-600/30">
                {authMode === 'signup' ? 'Complete Sign Up' : 'Log In'}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

ReactDOM.render(<App />, document.getElementById('root'));""",
            "api/server.js" to """// Node.js Express Mock Backend Server
const express = require('express');
const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', uptime: process.uptime(), server: 'Bolt Container' });
});

app.post('/api/deploy', (req, res) => {
  res.json({ success: true, deploymentId: 'dep_' + Date.now(), status: 'RUNNING' });
});

app.listen(PORT, () => {
  console.log('[Bolt Server] Listening on http://localhost:' + PORT);
});"""
        )
    )

    val KANBAN_BOARD = TemplateDefinition(
        id = "kanban_board",
        name = "Task Kanban & REST API",
        description = "Full-stack project management board with drag/status moves and simulated backend storage",
        iconName = "view_kanban",
        defaultActiveFile = "src/App.jsx",
        initialFiles = mapOf(
            "package.json" to """{
  "name": "bolt-kanban-fullstack",
  "version": "1.0.0",
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1"
  }
}""",
            "index.html" to """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Bolt Kanban Pro</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://unpkg.com/react@18/umd/react.production.min.js"></script>
  <script src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"></script>
  <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen">
  <div id="root"></div>
  <script type="text/babel" src="src/App.jsx"></script>
</body>
</html>""",
            "src/App.jsx" to """function KanbanApp() {
  const [tasks, setTasks] = React.useState([
    { id: 1, title: 'Architect Database Schemas', col: 'done', priority: 'high', tag: 'Backend' },
    { id: 2, title: 'Implement Gemini 3.1 Pro Thinking Mode', col: 'in_progress', priority: 'high', tag: 'AI' },
    { id: 3, title: 'Design In-App Virtual Terminal Shell', col: 'in_progress', priority: 'medium', tag: 'IDE' },
    { id: 4, title: 'Write WebSocket Live Sync API', col: 'todo', priority: 'low', tag: 'Network' }
  ]);
  const [newTitle, setNewTitle] = React.useState('');
  const [newTag, setNewTag] = React.useState('Frontend');

  const moveTask = (id, direction) => {
    const cols = ['todo', 'in_progress', 'done'];
    setTasks(tasks.map(t => {
      if (t.id === id) {
        const currentIdx = cols.indexOf(t.col);
        const nextIdx = Math.max(0, Math.min(cols.length - 1, currentIdx + direction));
        return { ...t, col: cols[nextIdx] };
      }
      return t;
    }));
  };

  const addTask = (e) => {
    e.preventDefault();
    if (!newTitle.trim()) return;
    setTasks([...tasks, {
      id: Date.now(),
      title: newTitle,
      col: 'todo',
      priority: 'medium',
      tag: newTag
    }]);
    setNewTitle('');
  };

  const cols = [
    { key: 'todo', name: 'To Do', color: 'border-amber-500/50' },
    { key: 'in_progress', name: 'In Progress', color: 'border-cyan-500/50' },
    { key: 'done', name: 'Completed', color: 'border-emerald-500/50' }
  ];

  return (
    <div class="p-6 max-w-6xl mx-auto">
      <header class="flex items-center justify-between mb-8 pb-4 border-b border-slate-800">
        <div>
          <h1 class="text-2xl font-bold flex items-center space-x-2">
            <span>📋 Project Sprint Kanban</span>
          </h1>
          <p class="text-xs text-slate-400 mt-1">Simulated Full-Stack State with Live Virtual Backend</p>
        </div>
        <div class="text-xs bg-slate-900 border border-slate-800 px-3 py-1.5 rounded-lg text-cyan-400">
          Total Tasks: {tasks.length}
        </div>
      </header>

      {/* Add Task Bar */}
      <form onSubmit={addTask} class="flex gap-2 mb-8 bg-slate-900 p-2 rounded-xl border border-slate-800">
        <input 
          value={newTitle} 
          onChange={e => setNewTitle(e.target.value)} 
          placeholder="New task title..." 
          class="flex-1 bg-transparent px-3 py-2 text-sm text-white focus:outline-none"
        />
        <select 
          value={newTag} 
          onChange={e => setNewTag(e.target.value)}
          class="bg-slate-800 text-xs px-3 py-2 rounded-lg text-slate-300 border border-slate-700"
        >
          <option>Frontend</option>
          <option>Backend</option>
          <option>AI</option>
          <option>DevOps</option>
        </select>
        <button type="submit" class="bg-violet-600 hover:bg-violet-500 text-white px-4 py-2 rounded-lg text-xs font-semibold">
          Add Task
        </button>
      </form>

      {/* Kanban Columns */}
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        {cols.map(c => {
          const colTasks = tasks.filter(t => t.col === c.key);
          return (
            <div key={c.key} class={"bg-slate-900/80 border rounded-2xl p-4 flex flex-col " + c.color}>
              <div class="flex items-center justify-between mb-4">
                <span class="font-bold text-sm">{c.name}</span>
                <span class="text-xs bg-slate-800 px-2 py-0.5 rounded-full">{colTasks.length}</span>
              </div>
              <div class="space-y-3 flex-1">
                {colTasks.map(t => (
                  <div key={t.id} class="bg-slate-800/90 border border-slate-700/60 p-3.5 rounded-xl shadow-sm">
                    <div class="flex justify-between items-start mb-2">
                      <span class="text-xs font-bold px-2 py-0.5 rounded bg-violet-950 text-violet-300 border border-violet-800">
                        {t.tag}
                      </span>
                      <span class="text-[10px] uppercase font-bold text-amber-400">{t.priority}</span>
                    </div>
                    <p class="text-sm font-medium text-slate-100">{t.title}</p>
                    <div class="flex justify-end gap-1 mt-3">
                      {c.key !== 'todo' && (
                        <button onClick={() => moveTask(t.id, -1)} class="text-xs bg-slate-700 hover:bg-slate-600 px-2 py-1 rounded text-slate-300">
                          ◀ Back
                        </button>
                      )}
                      {c.key !== 'done' && (
                        <button onClick={() => moveTask(t.id, 1)} class="text-xs bg-cyan-600 hover:bg-cyan-500 px-2 py-1 rounded text-white font-medium">
                          Next ▶
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

ReactDOM.render(<KanbanApp />, document.getElementById('root'));"""
        )
    )

    val RETRO_GAME = TemplateDefinition(
        id = "retro_game",
        name = "HTML5 Canvas Arcade (Breakout)",
        description = "Playable 60FPS retro neon arcade game with particle effects and touch controls",
        iconName = "sports_esports",
        defaultActiveFile = "index.html",
        initialFiles = mapOf(
            "index.html" to """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
  <title>Neon Breakout</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      background: #090D16;
      color: #E2E8F0;
      font-family: 'Courier New', monospace;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      overflow: hidden;
    }
    #gameCanvas {
      background: #020617;
      border: 2px solid #38BDF8;
      box-shadow: 0 0 20px rgba(56, 189, 248, 0.3);
      border-radius: 12px;
      touch-action: none;
    }
    .hud {
      display: flex;
      justify-content: space-between;
      width: 360px;
      padding: 10px 4px;
      font-size: 14px;
      font-weight: bold;
    }
    .instructions {
      font-size: 11px;
      color: #64748B;
      margin-top: 8px;
    }
  </style>
</head>
<body>
  <div class="hud">
    <span style="color: #38BDF8;">SCORE: <span id="score">0</span></span>
    <span style="color: #F43F5E;">LIVES: <span id="lives">3</span></span>
  </div>
  <canvas id="gameCanvas" width="360" height="460"></canvas>
  <div class="instructions">Drag or Move Mouse to Control Paddle</div>

  <script>
    const canvas = document.getElementById('gameCanvas');
    const ctx = canvas.getContext('2d');
    let score = 0;
    let lives = 3;

    let paddleWidth = 75;
    let paddleHeight = 10;
    let paddleX = (canvas.width - paddleWidth) / 2;

    let ballX = canvas.width / 2;
    let ballY = canvas.height - 30;
    let dx = 2.5;
    let dy = -2.5;
    let ballRadius = 6;

    const rowCount = 4;
    const colCount = 6;
    const brickWidth = 50;
    const brickHeight = 14;
    const brickPadding = 6;
    const brickOffsetTop = 40;
    const brickOffsetLeft = 14;

    const bricks = [];
    const colors = ['#F43F5E', '#FB923C', '#38BDF8', '#A855F7'];

    for (let c = 0; c < colCount; c++) {
      bricks[c] = [];
      for (let r = 0; r < rowCount; r++) {
        bricks[c][r] = { x: 0, y: 0, status: 1, color: colors[r] };
      }
    }

    // Touch and mouse handling
    function updatePaddle(xPos) {
      const rect = canvas.getBoundingClientRect();
      const relativeX = xPos - rect.left;
      if (relativeX > 0 && relativeX < canvas.width) {
        paddleX = Math.max(0, Math.min(canvas.width - paddleWidth, relativeX - paddleWidth / 2));
      }
    }

    canvas.addEventListener('mousemove', e => updatePaddle(e.clientX));
    canvas.addEventListener('touchmove', e => {
      if (e.touches.length > 0) {
        updatePaddle(e.touches[0].clientX);
      }
    });

    function collisionDetection() {
      for (let c = 0; c < colCount; c++) {
        for (let r = 0; r < rowCount; r++) {
          const b = bricks[c][r];
          if (b.status === 1) {
            if (ballX > b.x && ballX < b.x + brickWidth && ballY > b.y && ballY < b.y + brickHeight) {
              dy = -dy;
              b.status = 0;
              score += 10;
              document.getElementById('score').innerText = score;
            }
          }
        }
      }
    }

    function draw() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      // Draw bricks
      for (let c = 0; c < colCount; c++) {
        for (let r = 0; r < rowCount; r++) {
          if (bricks[c][r].status === 1) {
            const brickX = c * (brickWidth + brickPadding) + brickOffsetLeft;
            const brickY = r * (brickHeight + brickPadding) + brickOffsetTop;
            bricks[c][r].x = brickX;
            bricks[c][r].y = brickY;
            ctx.beginPath();
            ctx.roundRect(brickX, brickY, brickWidth, brickHeight, 3);
            ctx.fillStyle = bricks[c][r].color;
            ctx.fill();
            ctx.closePath();
          }
        }
      }

      // Draw Ball
      ctx.beginPath();
      ctx.arc(ballX, ballY, ballRadius, 0, Math.PI * 2);
      ctx.fillStyle = '#38BDF8';
      ctx.shadowBlur = 10;
      ctx.shadowColor = '#38BDF8';
      ctx.fill();
      ctx.closePath();
      ctx.shadowBlur = 0;

      // Draw Paddle
      ctx.beginPath();
      ctx.roundRect(paddleX, canvas.height - paddleHeight - 8, paddleWidth, paddleHeight, 5);
      ctx.fillStyle = '#A855F7';
      ctx.fill();
      ctx.closePath();

      collisionDetection();

      if (ballX + dx > canvas.width - ballRadius || ballX + dx < ballRadius) {
        dx = -dx;
      }
      if (ballY + dy < ballRadius) {
        dy = -dy;
      } else if (ballY + dy > canvas.height - paddleHeight - 8 - ballRadius) {
        if (ballX > paddleX && ballX < paddleX + paddleWidth) {
          dy = -Math.abs(dy);
        } else if (ballY + dy > canvas.height - ballRadius) {
          lives--;
          document.getElementById('lives').innerText = lives;
          if (!lives) {
            alert('GAME OVER! Final Score: ' + score);
            document.location.reload();
          } else {
            ballX = canvas.width / 2;
            ballY = canvas.height - 30;
            dx = 2.5;
            dy = -2.5;
            paddleX = (canvas.width - paddleWidth) / 2;
          }
        }
      }

      ballX += dx;
      ballY += dy;
      requestAnimationFrame(draw);
    }

    draw();
  </script>
</body>
</html>"""
        )
    )

    val ALL = listOf(REACT_SAAS, KANBAN_BOARD, RETRO_GAME)
}
