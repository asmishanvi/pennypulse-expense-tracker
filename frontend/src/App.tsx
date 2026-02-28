import React, { useEffect, useState } from 'react';
import { api } from './api/client';

interface Summary {
  count: number;
  total: number;
  average: number;
}

interface CategoryTotal {
  category: string;
  total: number;
}

export default function App() {
  const [summary, setSummary] = useState<Summary | null>(null);
  const [categories, setCategories] = useState<CategoryTotal[]>([]);

  useEffect(() => {
    const month = new Date().toISOString().slice(0, 7);
    api.get(`/analytics/summary?month=${month}`).then((res) => setSummary(res.data));
    api.get(`/analytics/category?month=${month}`).then((res) => setCategories(res.data));
  }, []);

  return (
    <div className="container">
      <div className="header">
        <div>
          <h1>PennyPulse</h1>
          <small>Full-stack expense management dashboard</small>
        </div>
        <span className="badge">Live</span>
      </div>

      <div className="grid">
        <div className="card">
          <h3>Monthly Summary</h3>
          {summary ? (
            <div className="list">
              <div className="row"><span>Total</span><strong>${summary.total.toFixed(2)}</strong></div>
              <div className="row"><span>Average</span><strong>${summary.average.toFixed(2)}</strong></div>
              <div className="row"><span>Transactions</span><strong>{summary.count}</strong></div>
            </div>
          ) : (
            <small>Loading…</small>
          )}
        </div>

        <div className="card">
          <h3>Top Categories</h3>
          <div className="list">
            {categories.slice(0, 5).map((c) => (
              <div key={c.category} className="row">
                <span>{c.category}</span>
                <strong>${c.total.toFixed(2)}</strong>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
