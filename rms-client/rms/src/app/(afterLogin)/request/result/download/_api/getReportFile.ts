export async function getReportFile(reportId: string) {
    return await fetch(`/w-api/result-service/reports/${reportId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store',
        headers: {
            'Accept': 'application/pdf, application/vnd.ms-excel, image/jpeg, image/png'
        }
    });
}