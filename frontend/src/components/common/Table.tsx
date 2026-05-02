import type { ReactNode } from 'react'

interface Column<T> {
  header: string
  render: (row: T) => ReactNode
}

interface TableProps<T> {
  columns: Column<T>[]
  rows: T[]
  keyExtractor: (row: T) => string
  emptyMessage?: string
}

export function Table<T>({ columns, rows, keyExtractor, emptyMessage = 'Nenhum registro encontrado.' }: TableProps<T>) {
  return (
    <div className="overflow-x-auto rounded-lg border border-gray-200">
      <table className="w-full text-sm text-left">
        <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
          <tr>
            {columns.map(col => (
              <th key={col.header} className="px-4 py-3">{col.header}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className="px-4 py-6 text-center text-gray-400">{emptyMessage}</td>
            </tr>
          ) : rows.map(row => (
            <tr key={keyExtractor(row)} className="border-t border-gray-100 hover:bg-gray-50">
              {columns.map(col => (
                <td key={col.header} className="px-4 py-3">{col.render(row)}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
