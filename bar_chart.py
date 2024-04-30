import pandas as pd
import matplotlib.pyplot as plt

# Read the benchmark table into a pandas DataFrame
df = pd.read_csv("benchmark.txt", sep='\\s+')

# Extract the benchmark method names and scores
df['Benchmark'] = df['Benchmark'].str.split('.').str[-1]

print(df)
max_threads = 12

# throughput
total_count = 10**9
df['throughput'] = total_count / df['Score']

byte_buffer = df[df['Benchmark'] == "byteBuffer"]
mmap = df[df['Benchmark'] == "mmap"]

score1 = byte_buffer[byte_buffer['(concurrency)'] == 1].iloc[0]['Score']
byte_buffer['acceleration'] = score1 / byte_buffer['Score']

bar_df = df[df['(concurrency)'] == 0]

fig, ax = plt.subplots()
ax.bar(bar_df['Benchmark'], bar_df['Score'])
ax.set_ylabel('Seconds')
ax.set_title('Runtime across multiple versions')
plt.show()

# plot 2 charts on the left, aligned vertically and 1 chart on the right, merging the two cells
fig, axes = plt.subplots(1, 3)

# plot the data on the first subplot
fig.suptitle('Number of threads', y=0.05)

# Total time
axes[0].set_xticks(range(1, max_threads+1))
axes[0].plot(byte_buffer['(concurrency)'], byte_buffer['Score'])
axes[0].set_ylabel('Seconds')
axes[0].set_title('Total time')

# Throughput
axes[1].set_xticks(range(1, max_threads+1))
axes[1].plot(byte_buffer['(concurrency)'], byte_buffer['throughput'])
axes[1].set_ylabel('Rows Per Second')
axes[1].set_title('Throughput')

# Acceleration
axes[2].set_xticks(range(1, max_threads+1))
axes[2].set_yticks(range(1, max_threads+1))
axes[2].plot(byte_buffer['(concurrency)'], byte_buffer['acceleration'], label='Actual')
axes[2].plot(byte_buffer['(concurrency)'], byte_buffer['(concurrency)'], label='Linear')
axes[2].set_aspect('equal')
axes[2].set_title('Gain')
axes[2].legend()

plt.show()