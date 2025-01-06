import type {Request} from "@/model/Request";

export async function getReportFiles(requests: Request[]) {
    return await fetch(`/w-api/result-service/reports`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            'Accept': 'application/zip'
        },
        body: JSON.stringify(requests),
        credentials: 'include',
        cache: 'no-store'
    });
}