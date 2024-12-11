import type {Request} from "@/model/Request";

export async function getReportFiles(requests: Request[]) {
    const res = await fetch(`/w-api/result-service/reports`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            'Accept': 'application/zip'
        },
        body: JSON.stringify(requests),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}