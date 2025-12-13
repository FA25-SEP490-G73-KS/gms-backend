#!/usr/bin/env python3
"""
Export all test case matrices (1-20) to Excel file.
Reads from TEST_CASE_DESIGN_DOCUMENT.md and exports to Excel format.
"""

import re
import csv
import sys

def parse_matrix_from_md(md_content, matrix_num):
    """Extract matrix content from markdown."""
    pattern = rf'### MATRIX {matrix_num}:.*?\n\n(.*?)(?=\n---|\n### MATRIX|\Z)'
    match = re.search(pattern, md_content, re.DOTALL)
    if match:
        return match.group(1).strip()
    return None

def extract_matrix_table(matrix_content):
    """Extract table rows from matrix content."""
    lines = matrix_content.split('\n')
    table_rows = []
    in_table = False
    
    for line in lines:
        if '| Condition Precondition |' in line or '|------------------------|' in line:
            in_table = True
            if '| Condition Precondition |' in line:
                # Header row
                headers = [cell.strip() for cell in line.split('|')[1:-1]]
                table_rows.append(headers)
        elif in_table:
            if line.strip().startswith('|') and '---' not in line:
                cells = [cell.strip() for cell in line.split('|')[1:-1]]
                if cells and any(cell for cell in cells):  # Skip empty rows
                    table_rows.append(cells)
            elif line.strip() == '' or line.strip().startswith('**'):
                # End of table
                break
    
    return table_rows

def extract_matrix_info(matrix_content):
    """Extract matrix metadata (Function Code, Function Name, Summary)."""
    info = {}
    
    # Extract Function Code
    func_code_match = re.search(r'\*\*Function Code:\*\*\s*(\S+)', matrix_content)
    if func_code_match:
        info['function_code'] = func_code_match.group(1)
    
    # Extract Function Name
    func_name_match = re.search(r'\*\*Function Name:\*\*\s*(\S+)', matrix_content)
    if func_name_match:
        info['function_name'] = func_name_match.group(1)
    
    # Extract Summary
    summary_match = re.search(r'\*\*SUMMARY:\*\*\s*\n(.*?)\n', matrix_content, re.DOTALL)
    if summary_match:
        info['summary'] = summary_match.group(1).strip()
    
    return info

def main():
    # Read TEST_CASE_DESIGN_DOCUMENT.md
    try:
        with open('TEST_CASE_DESIGN_DOCUMENT.md', 'r', encoding='utf-8') as f:
            md_content = f.read()
    except FileNotFoundError:
        print("Error: TEST_CASE_DESIGN_DOCUMENT.md not found")
        sys.exit(1)
    
    # Extract all matrices (1-20)
    all_matrices_data = []
    
    for matrix_num in range(1, 21):
        matrix_content = parse_matrix_from_md(md_content, matrix_num)
        if matrix_content:
            info = extract_matrix_info(matrix_content)
            table_rows = extract_matrix_table(matrix_content)
            
            if table_rows:
                all_matrices_data.append({
                    'matrix_num': matrix_num,
                    'info': info,
                    'table': table_rows
                })
                print(f"Extracted Matrix {matrix_num}: {info.get('function_name', 'N/A')}")
    
    # Write to TSV file (Excel-compatible)
    tsv_file = 'test_case_matrices_all.tsv'
    excel_file = 'test_case_matrices_all.xlsx'
    
    with open(tsv_file, 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f, delimiter='\t')
        
        for matrix_data in all_matrices_data:
            matrix_num = matrix_data['matrix_num']
            info = matrix_data['info']
            table = matrix_data['table']
            
            # Write matrix header
            writer.writerow([])
            writer.writerow([f'MATRIX {matrix_num}'])
            writer.writerow([f"Function Code: {info.get('function_code', 'N/A')}"])
            writer.writerow([f"Function Name: {info.get('function_name', 'N/A')}"])
            writer.writerow([f"Summary: {info.get('summary', 'N/A')}"])
            writer.writerow([])
            
            # Write table
            for row in table:
                writer.writerow(row)
            
            writer.writerow([])
            writer.writerow(['---'])
            writer.writerow([])
    
    sys.stdout.buffer.write(f"\nTSV file created: {tsv_file}\n".encode('utf-8'))
    sys.stdout.buffer.write(f"Total matrices exported: {len(all_matrices_data)}\n".encode('utf-8'))
    sys.stdout.buffer.write(f"\nTo open in Excel:\n".encode('utf-8'))
    sys.stdout.buffer.write(f"   1. Open Excel\n".encode('utf-8'))
    sys.stdout.buffer.write(f"   2. File > Open > Select '{tsv_file}'\n".encode('utf-8'))
    sys.stdout.buffer.write(f"   3. Choose 'Tab' as delimiter\n".encode('utf-8'))
    sys.stdout.buffer.write(f"   4. Save as .xlsx format\n".encode('utf-8'))

if __name__ == "__main__":
    main()

