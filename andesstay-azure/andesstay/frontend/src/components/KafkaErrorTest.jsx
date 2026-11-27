import { useState } from "react";
import { KafkaApi } from "../api/client";

export default function KafkaErrorTest() {
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  async function trigger() {
    setLoading(true);
    setResult(null);
    setError(null);
    try {
      setResult(await KafkaApi.simulateError());
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card">
      <h3>Prueba de error en Kafka</h3>
      <p className="muted small">
        Envía un evento inválido al topic <code>reservations.events</code>. Los consumidores
        (auditoría y reportes) no podrán leerlo y registrarán el error en sus logs; los eventos
        siguientes se siguen procesando con normalidad.
      </p>
      <button className="btn-danger" onClick={trigger} disabled={loading}>
        {loading ? "Enviando…" : "Provocar error en Kafka"}
      </button>

      {result && (
        <p className="warning-box">
          {result.message}
          <br />
          <span className="small">Topic {result.topic} · partición {result.partition} · offset {result.offset}</span>
        </p>
      )}
      {error && <p className="error">Error: {error}</p>}
    </div>
  );
}