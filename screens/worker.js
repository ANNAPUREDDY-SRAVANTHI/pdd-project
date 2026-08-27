// ============================================================
//  WORKER DASHBOARD
// ============================================================
route('/worker-dashboard', async (el) => {
  if (!API.isLoggedIn()) { navigate('/login'); return; }
  STATE.currentUser = API.getUser(); STATE.currentRole = 'Worker';
  if (!STATE.workers.length) await refreshAll();
  renderWorkerDashboard(el);
  if (STATE.pollInterval) clearInterval(STATE.pollInterval);
  STATE.pollInterval = setInterval(async () => { await refreshAll(); renderWorkerDashboard(el); }, 15000);
});

function renderWorkerDashboard(el) {
  const loggedUser = API.getUser();
  const me = STATE.workers.find(w => w.phone === loggedUser || w.name === loggedUser);
  const wName = me?.name || 'Worker';
  const wId = me?.id || -1;
  const todayMap = STATE.todayAttendance();
  const todayStatus = todayMap[wId] || 'Not Marked';
  const days = STATE.getPresentDaysCount(wId);
  const earnings = STATE.getTotalEarned(wId);
  const notifCount = STATE.notifications.length;

  el.innerHTML = `
    <div class="top-bar">
      <div class="top-bar-col">
        <span class="top-bar-title">Hello, ${wName}!</span>
        <span class="top-bar-subtitle">Your Farm Portal</span>
      </div>
      <button class="icon-btn" onclick="navigate('/notifications')" title="Notifications">
        <div class="badge-wrap">
          ${ICONS.notif}
          ${notifCount > 0 ? `<span class="badge">${notifCount}</span>` : ''}
        </div>
      </button>
      <button class="icon-btn danger" onclick="doLogout()" title="Logout">${ICONS.logout}</button>
    </div>
    <div class="content with-bottom-nav">
      <!-- Today's Status -->
      <div class="card mb-16" style="background:${todayStatus === 'Present' ? 'var(--green-light)' : '#fff'}">
        <div class="fs-sm text-gray fw-bold mb-8">Today's Status</div>
        <div style="display:flex;align-items:center;gap:8px">
          <span style="color:${todayStatus === 'Present' ? 'var(--green-primary)' : 'var(--orange)'};font-size:20px">
            ${todayStatus === 'Present' ? '✅' : 'ℹ️'}
          </span>
          <span class="fw-bold">You are: ${todayStatus}</span>
        </div>
      </div>
      <!-- Earnings -->
      <div class="card mb-16">
        <div class="fs-sm text-gray fw-bold mb-8">Earnings Summary</div>
        <div class="flex-between">
          <div>
            <div class="fs-sm text-gray">Total Earned (${days} days)</div>
            <div style="font-size:28px;font-weight:900;color:var(--green-dark)">₹ ${Math.round(earnings)}</div>
          </div>
          <button class="btn btn-primary btn-sm" onclick="navigate('/my-wages')">Report</button>
        </div>
      </div>
      <!-- Daily Duty -->
      <div class="card mb-16">
        <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px">
          <span style="color:var(--green-primary)">${ICONS.assignment.replace('viewBox','style="width:20px;height:20px" viewBox')}</span>
          <div class="fs-sm text-gray fw-bold">My Daily Duty</div>
        </div>
        <div style="font-size:18px;font-weight:800;color:var(--green-dark)">${me?.role || 'General Labor'}</div>
      </div>
    </div>
    ${workerBottomNav(0)}`;
}

function workerBottomNav(active) {
  const items = [
    { icon: ICONS.home, label: 'Home', route: '/worker-dashboard' },
    { icon: ICONS.fact_check, label: 'Attendance', route: '/my-attendance' },
    { icon: ICONS.payments, label: 'Finance', route: '/my-wages' },
    { icon: ICONS.assignment, label: 'Tasks', route: '/my-tasks' },
    { icon: ICONS.person, label: 'Profile', route: '/worker-profile' },
  ];
  return `<nav class="bottom-nav">
    ${items.map((item, i) => `
      <button class="nav-item ${i === active ? 'active' : ''}" onclick="navigate('${item.route}')">
        <div class="nav-indicator">${item.icon.replace('viewBox','style="width:22px;height:22px" viewBox')}</div>
        <span>${item.label}</span>
      </button>`).join('')}
  </nav>`;
}

// ============================================================
//  MY ATTENDANCE SCREEN
// ============================================================
route('/my-attendance', async (el) => {
  if (!STATE.workers.length) await refreshAll();
  const me = STATE.workers.find(w => w.phone === API.getUser() || w.name === API.getUser());
  const wId = me?.id || -1;
  const myRecords = STATE.attendance.filter(r => r.worker === wId)
    .sort((a, b) => b.date.localeCompare(a.date));
  const presentDays = myRecords.filter(r => r.status === 'Present').length;
  const absentDays = myRecords.filter(r => r.status === 'Absent').length;
  el.innerHTML = `
    <div class="top-bar">
      <button class="icon-btn" onclick="navigate('/worker-dashboard')">${ICONS.back}</button>
      <span class="top-bar-title">My Attendance</span>
    </div>
    <div class="content with-bottom-nav">
      <div class="card-row">
        <div class="stat-card green"><div class="stat-value">${presentDays}</div><div class="stat-label">Days Present</div></div>
        <div class="stat-card" style="background:#fdeaea;flex:1"><div class="stat-value" style="color:var(--red)">${absentDays}</div><div class="stat-label">Days Absent</div></div>
      </div>
      ${myRecords.length === 0 ? `<div class="empty-state"><div class="empty-icon">📋</div><p>No attendance records yet.</p></div>` :
        myRecords.map(r => `
          <div style="background:#fff;border-radius:16px;padding:14px 16px;margin-bottom:10px;display:flex;align-items:center;justify-content:space-between;box-shadow:var(--card-shadow)">
            <div>
              <div class="fw-bold">${r.date}</div>
            </div>
            <span class="chip ${r.status === 'Present' ? 'chip-present' : 'chip-absent'}">${r.status}</span>
          </div>`).join('')}
    </div>
    ${workerBottomNav(1)}`;
});

// ============================================================
//  MY WAGES SCREEN
// ============================================================
route('/my-wages', async (el) => {
  if (!STATE.workers.length) await refreshAll();
  const me = STATE.workers.find(w => w.phone === API.getUser() || w.name === API.getUser());
  const wId = me?.id || -1;
  const earned = STATE.getTotalEarned(wId);
  const paid = STATE.getTotalPaid(wId);
  const due = STATE.getBalanceDue(wId);
  const days = STATE.getPresentDaysCount(wId);
  const myPayments = STATE.payments.filter(p => p.worker === wId).sort((a, b) => (b.date || '').localeCompare(a.date || ''));
  el.innerHTML = `
    <div class="top-bar">
      <button class="icon-btn" onclick="navigate('/worker-dashboard')">${ICONS.back}</button>
      <span class="top-bar-title">My Wages</span>
    </div>
    <div class="content with-bottom-nav">
      <div class="finance-header-card">
        <div class="label">Balance Due</div>
        <div class="amount" style="color:${due > 0 ? '#ffcdd2' : '#c8e6c9'}">₹ ${due.toFixed(2)}</div>
        <div class="sublabel">Amount still owed to you</div>
      </div>
      <div class="card-row">
        <div class="stat-card green"><div class="stat-value">₹${Math.round(earned)}</div><div class="stat-label">Total Earned</div></div>
        <div class="stat-card blue"><div class="stat-value">₹${Math.round(paid)}</div><div class="stat-label">Total Paid</div></div>
      </div>
      <div class="card mb-16">
        <div class="fs-sm text-gray">Daily Wage: ₹${me?.daily_wage || 0} × ${days} days present</div>
      </div>
      <div class="section-title">Payment History</div>
      ${myPayments.length === 0 ? `<div class="empty-state"><div class="empty-icon">💳</div><p>No payments recorded yet.</p></div>` :
        myPayments.map(p => `
          <div style="background:#fff;border-radius:16px;padding:14px 16px;margin-bottom:10px;display:flex;align-items:center;justify-content:space-between;box-shadow:var(--card-shadow)">
            <div>
              <div class="fw-bold">₹${Math.round(p.amount)}</div>
              <div class="fs-sm text-gray">${p.description || 'Salary Payment'}</div>
            </div>
            <span class="fs-sm text-gray">${p.date || 'N/A'}</span>
          </div>`).join('')}
    </div>
    ${workerBottomNav(2)}`;
});

// ============================================================
//  MY TASKS SCREEN (Worker)
// ============================================================
route('/my-tasks', async (el) => {
  if (!STATE.workers.length) await refreshAll();
  const me = STATE.workers.find(w => w.phone === API.getUser() || w.name === API.getUser());
  const wId = me?.id || -1;
  const myTasks = STATE.tasks.filter(t => t.worker === wId).reverse();
  renderMyTasks(el, myTasks, wId);
});
function renderMyTasks(el, myTasks, wId) {
  el.innerHTML = `
    <div class="top-bar">
      <button class="icon-btn" onclick="navigate('/worker-dashboard')">${ICONS.back}</button>
      <span class="top-bar-title">My Tasks</span>
    </div>
    <div class="content with-bottom-nav">
      <div class="section-title">Assigned Work</div>
      ${myTasks.length === 0 ? `<div class="empty-state"><div class="empty-icon">📋</div><p>No tasks assigned yet.</p></div>` :
        myTasks.map(t => `
          <div class="task-card">
            <div class="task-info" style="flex:1">
              <div class="fw-bold">${t.is_completed ? '✅' : '⏳'} Task #${t.id}</div>
              <div class="task-desc">${t.description}</div>
              <div class="fs-sm text-gray">${t.date || ''}</div>
            </div>
            ${!t.is_completed ? `<button class="btn btn-primary btn-sm" onclick="markComplete(${t.id})">Done</button>` : `<span class="chip chip-present">Done</span>`}
          </div>`).join('')}
    </div>
    ${workerBottomNav(3)}`;
  window.markComplete = async (taskId) => {
    await API.updateTask(taskId, { is_completed: true });
    await refreshAll();
    const me = STATE.workers.find(w => w.phone === API.getUser() || w.name === API.getUser());
    const wId2 = me?.id || -1;
    const updated = STATE.tasks.filter(t => t.worker === wId2).reverse();
    renderMyTasks(el, updated, wId2);
    showToast('Task Completed! 🌟');
  };
}

// ============================================================
//  WORKER PROFILE SCREEN
// ============================================================
route('/worker-profile', async (el) => {
  if (!STATE.workers.length) await refreshAll();
  const me = STATE.workers.find(w => w.phone === API.getUser() || w.name === API.getUser());
  const daysPresent = STATE.getPresentDaysCount(me?.id || -1);
  el.innerHTML = `
    <div class="top-bar">
      <button class="icon-btn" onclick="navigate('/worker-dashboard')">${ICONS.back}</button>
      <span class="top-bar-title">My Profile</span>
      <button class="icon-btn danger" onclick="doLogout()">${ICONS.logout}</button>
    </div>
    <div class="content with-bottom-nav">
      <div class="profile-header">
        <div class="avatar large">${avatarLetter(me?.name)}</div>
        <div style="font-size:22px;font-weight:800;color:var(--green-dark)">${me?.name || 'Worker'}</div>
        <div class="text-gray">Worker ID: #${me?.id || '0000'}</div>
      </div>
      <div class="card mb-16 profile-info-card">
        <div class="fw-bold text-dark mb-16">Information</div>
        <div class="info-row"><span class="info-icon">${ICONS.phone.replace('viewBox','style="width:20px;height:20px" viewBox')}</span>
          <div><div class="info-label">Phone</div><div class="info-value">${me?.phone || 'Not set'}</div></div></div>
        <div class="info-row"><span class="info-icon">${ICONS.home.replace('viewBox','style="width:20px;height:20px" viewBox')}</span>
          <div><div class="info-label">Address</div><div class="info-value">${me?.address || 'Not set'}</div></div></div>
        <div class="info-row"><span class="info-icon">${ICONS.work.replace('viewBox','style="width:20px;height:20px" viewBox')}</span>
          <div><div class="info-label">Role</div><div class="info-value">${me?.role || 'General Labor'}</div></div></div>
        <div class="info-row"><span class="info-icon">${ICONS.calendar.replace('viewBox','style="width:20px;height:20px" viewBox')}</span>
          <div><div class="info-label">Joining Date</div><div class="info-value">${me?.join_date || 'N/A'}</div></div></div>
      </div>
      <div class="card" style="background:var(--green-light)">
        <div class="flex-between">
          <div>
            <div class="fs-sm text-green fw-bold">Attendance Count</div>
            <div style="font-size:28px;font-weight:900;color:var(--green-dark)">${daysPresent} Days</div>
          </div>
          <span style="color:var(--green-primary)">${ICONS.fact_check.replace('viewBox','style="width:50px;height:50px" viewBox')}</span>
        </div>
      </div>
    </div>
    ${workerBottomNav(4)}`;
});

// ============================================================
//  NOTIFICATIONS SCREEN
// ============================================================
route('/notifications', async (el) => {
  if (!STATE.notifications.length) await refreshAll();
  el.innerHTML = `
    <div class="top-bar">
      <button class="icon-btn" onclick="history.back()">${ICONS.back}</button>
      <span class="top-bar-title" style="color:var(--green-dark)">Farm Alerts</span>
    </div>
    <div class="content">
      ${STATE.notifications.length === 0 ? `
        <div class="empty-state">
          <div class="empty-icon">${ICONS.notif_off.replace('viewBox','style="width:64px;height:64px;color:#ccc" viewBox')}</div>
          <p>No notifications for today</p>
        </div>` :
        [...STATE.notifications].reverse().map(n => {
          let icon = ICONS.notif, tint = 'var(--orange)', bg = '#fff3e0';
          if (n.title.toLowerCase().includes('task')) { icon = ICONS.work; tint = 'var(--blue)'; bg = 'var(--blue-light)'; }
          if (n.title.toLowerCase().includes('payment')) { icon = ICONS.payments; tint = 'var(--green-primary)'; bg = 'var(--green-light)'; }
          if (n.title.toLowerCase().includes('attendance')) { icon = ICONS.fact_check; tint = 'var(--green-mid)'; bg = 'var(--green-light)'; }
          const dateStr = (n.created_at || '').slice(0, 10);
          return `<div class="notif-card">
            <div class="notif-icon-wrap" style="background:${bg}">
              <span style="color:${tint}">${icon.replace('viewBox','style="width:24px;height:24px" viewBox')}</span>
            </div>
            <div style="flex:1">
              <div class="notif-title">${n.title}</div>
              <div class="notif-msg">${n.message}</div>
              <span class="notif-date">${dateStr}</span>
            </div>
          </div>`;
        }).join('')}
    </div>`;
});

// ============================================================
//  LOGOUT HELPER
// ============================================================
function doLogout() {
  if (STATE.pollInterval) { clearInterval(STATE.pollInterval); STATE.pollInterval = null; }
  API.clearToken();
  STATE.workers = []; STATE.attendance = []; STATE.payments = []; STATE.tasks = []; STATE.notifications = [];
  navigate('/login');
}
